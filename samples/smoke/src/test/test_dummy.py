"""Smoke test suite."""

import unittest


class TestDummy(unittest.TestCase):
    """Basic smoke test case."""

    def test_noop(self):
        """Verify the test harness runs."""
        self.assertTrue(True)
