#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from hspylib.core.metaclass.classpath import Classpath
from hspylib.core.tools.commons import parent_path, root_dir


class _Classpath(Classpath):
    """A class for managing classpath-related operations. Uses the Classpath metaclass."""

    def __init__(self):
        super().__init__(parent_path(__file__), parent_path(root_dir()), (parent_path(__file__) / "resources"))


# Instantiate the classpath singleton
assert (classpath := _Classpath().INSTANCE) is not None, "Failed to create Classpath instance"
