#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
   @project: hspylib
   @package: hspylib
      @file: __main__.py
   @created: Fri, 5 Jan 2024
    @author: <B>H</B>ugo <B>S</B>aporetti <B>J</B>unior
      @site: https://github.com/yorevs/askai
   @license: MIT - Please refer to <https://opensource.org/licenses/MIT>

   Copyright (c) 2024, HomeSetup
"""
import logging as log
import os
import sys
from pathlib import Path
from textwrap import dedent

from clitt.core.tui.tui_application import TUIApplication
from hspylib.core.enums.charset import Charset
from hspylib.core.tools.commons import sysout
from hspylib.core.zoned_datetime import now
from hspylib.modules.application.exit_status import ExitStatus
from hspylib.modules.application.version import Version
from __classpath__ import classpath


class Main(TUIApplication):
    """TODO"""

    # The welcome message
    DESCRIPTION: str = classpath.get_source("welcome.txt").read_text(encoding=Charset.UTF_8.val)

    # Location of the .version file
    VERSION: Version = Version.load(load_dir=classpath.source_path())

    # The resources folder
    RESOURCE_DIR: str = str(classpath.resource_path())

    INSTANCE: "Main"

    def __init__(self, app_name: str):
        super().__init__(app_name, self.VERSION, self.DESCRIPTION.format(self.VERSION), resource_dir=self.RESOURCE_DIR)

    def _setup_arguments(self) -> None:
        """Initialize application parameters and options."""
        ...

    def _main(self, *params, **kwargs) -> ExitStatus:
        """Run the application with the command line arguments."""

        log.info(
            dedent(
                f"""

        Settings ==============================
                STARTED: {now("%Y-%m-%d %H:%M:%S")}
        {self.configs}
        """
            )
        )
        return self._exec_application()

    def _exec_application(self) -> ExitStatus:
        """Execute the application main flow."""
        sysout(Main.DESCRIPTION)
        sysout(self.configs['pygradle.sample.message'])
        sysout(Path(os.path.join(self.RESOURCE_DIR), 'welcome.md').read_text(encoding=Charset.UTF_8.val), markdown=True)

        return ExitStatus.SUCCESS


# Application entry point
if __name__ == "__main__":
    Main("PyGradleProject").INSTANCE.run(sys.argv[1:])
