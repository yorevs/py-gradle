package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to sync Python package __init__.py files.
 */
class SyncPythonPackagesTask extends PyGradleBaseTask {
  private static final String INIT_TEMPLATE = """# _*_ coding: utf-8 _*_
#
# \$app_name v%version%
#
# Package: %package%
\"\"\"Package initialization.\"\"\"

%imports%
%lazy_exports%
__all__ = %modules%
__version__ = '%version%'
"""

  /**
   * Create a new sync python packages task.
   */
  SyncPythonPackagesTask() {
    group = 'Build'
    description = 'Find and update __init__ files'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncPythonPackages() {
    def extension = getExtension()
    project.file(extension.sourceRoot).traverse(type: groovy.io.FileType.DIRECTORIES) { directory ->
      directory.eachFileMatch(~/^__init__\.py$/) { initFile ->
        def pkg = initFile.getCanonicalPath()
          .replaceAll("${extension.sourceRoot}/", '')
          .replaceAll('\\/', '\\.')
          .replaceAll('\\.?__init__\\.py', '')
        def modules = []
        def exports = []
        def exportedSymbols = []
        initFile.getParentFile().eachFile() { module ->
          if (!module.name.startsWith('__') &&
              !module.name.endsWith('setup.py') &&
              ((module.isFile() && module.name.endsWith('.py')) ||
              new File(module.getCanonicalPath() + '/__init__.py').exists())) {
            if (!module.isFile() && extension.verbose) {
              println("Syncing package: ${initFile.getParentFile()}/${module.name}")
            }
            def moduleName = module.name.replaceAll('\\.py', '')
            modules << "'${moduleName}'"
            if (module.isFile() && module.name.endsWith('.py')) {
              collectExports(module, moduleName, exports, exportedSymbols)
            } else if (module.isDirectory()) {
              collectSubpackageExports(module, moduleName, exports, exportedSymbols)
            }
          }
        }
        def verFile = new File(extension.versionFile)
        def curVersion = verFile.getText().trim()
        def allList = formatAllList((modules + exportedSymbols).sort())
        def content = INIT_TEMPLATE
          .replace('%imports%', buildImportsBlock(exports))
          .replace('%lazy_exports%', buildLazyExportsBlock(exports))
          .replace('%package%', pkg)
          .replace('%modules%', allList)
          .replace('%version%', curVersion)
        initFile.write content
      }
    }
  }

  /**
   * Convert a snake_case module name to CamelCase.
   *
   * @param moduleName Module name.
   * @return CamelCase class name.
   */
  protected String toCamelCase(String moduleName) {
    moduleName.split('_').collect { part ->
      part ? part[0].toUpperCase() + part.substring(1) : ''
    }.join('')
  }

  /**
   * Find class names in a module file.
   *
   * @param moduleFile Module file.
   * @return Class names list.
   */
  protected List<String> moduleClassNames(File moduleFile) {
    def pattern = ~/^\s*class\s+([A-Za-z_][A-Za-z0-9_]*)\b/
    def names = []
    moduleFile.readLines().each { line ->
      def matcher = (line =~ pattern)
      if (matcher) {
        names << matcher[0][1]
      }
    }
    names
  }

  /**
   * Find constant names in a module file.
   *
   * @param moduleFile Module file.
   * @return Constant names list.
   */
  protected List<String> moduleConstantNames(File moduleFile) {
    def pattern = ~/^\s*([A-Z][A-Z0-9_]*)\s*=/
    def names = []
    moduleFile.readLines().each { line ->
      def matcher = (line =~ pattern)
      if (matcher) {
        names << matcher[0][1]
      }
    }
    names
  }

  /**
   * Build the import block for __init__.py.
   *
   * @param imports Import lines.
   * @return Import block string.
   */
  protected String buildImportsBlock(List<Map<String, String>> exports) {
    if (!exports) {
      return ''
    }
    def lines = ['import importlib', 'from typing import TYPE_CHECKING', '', 'if TYPE_CHECKING:']
    exports.sort { it.name }.each { entry ->
      lines << "    from .${entry.module} import ${entry.name}"
    }
    lines.join('\n') + '\n\n'
  }

  /**
   * Build lazy export helpers for __init__.py.
   *
   * @param exports Export entries.
   * @return Lazy export block string.
   */
  protected String buildLazyExportsBlock(List<Map<String, String>> exports) {
    if (!exports) {
      return ''
    }
    def entries = exports.sort { it.name }.collect { entry ->
      "    '${entry.name}': ('${entry.module}', '${entry.name}')"
    }
    def mapBlock = "_EXPORTS = {\n" + entries.join(',\n') + "\n}\n"
    def funcBlock = '''def __getattr__(name):
    if name in _EXPORTS:
        module, attr = _EXPORTS[name]
        mod = importlib.import_module(f"{__name__}.{module}")
        value = getattr(mod, attr)
        globals()[name] = value
        return value
    raise AttributeError(f"module {__name__!r} has no attribute {name!r}")
'''
    mapBlock + '\n' + funcBlock + '\n'
  }

  /**
   * Collect exports from a module file based on class name.
   *
   * @param moduleFile Module file.
   * @param modulePath Module path segment.
   * @param exports Export entries.
   * @param exportedSymbols Exported symbol names.
   */
  protected void collectExports(
      File moduleFile,
      String modulePath,
      List<Map<String, String>> exports,
      List<String> exportedSymbols
  ) {
    def names = []
    names.addAll(this.moduleClassNames(moduleFile))
    names.addAll(this.moduleConstantNames(moduleFile))
    names.unique().each { name ->
      exports << [module: modulePath, name: name]
      exportedSymbols << "'${name}'"
    }
  }

  /**
   * Collect exports from a subpackage directory.
   *
   * @param subpackageDir Subpackage directory.
   * @param subpackageName Subpackage name.
   * @param exports Export entries.
   * @param exportedSymbols Exported symbol names.
   */
  protected void collectSubpackageExports(
      File subpackageDir,
      String subpackageName,
      List<Map<String, String>> exports,
      List<String> exportedSymbols
  ) {
    subpackageDir.eachFile { File child ->
      if (child.isFile() && child.name.endsWith('.py') && !child.name.startsWith('__')) {
        def childModule = child.name.replaceAll('\\.py', '')
        def modulePath = "${subpackageName}.${childModule}"
        collectExports(child, modulePath, exports, exportedSymbols)
      }
    }
  }

  /**
   * Format the __all__ list with indentation.
   *
   * @param entries List of entries.
   * @return Formatted list string.
   */
  protected String formatAllList(List<String> entries) {
    if (!entries) {
      return '[]'
    }
    def formatted = entries.collect { "    ${it}" }.join(',\n')
    "[\n${formatted}\n]"
  }

  /**
   * Get the source root directory.
   *
   * @return Source root directory.
   */
  @InputDirectory
  File getSourceRootDir() {
    project.file(getExtension().sourceRoot)
  }

  /**
   * Get the output directory for updated packages.
   *
   * @return Output directory.
   */
  @OutputDirectory
  File getOutputRootDir() {
    project.file(getExtension().sourceRoot)
  }
}
