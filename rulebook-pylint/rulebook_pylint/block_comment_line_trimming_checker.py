import re
from typing import TYPE_CHECKING

from astroid import Const, Module, ClassDef, FunctionDef
from pylint.typing import MessageDefinitionTuple
from rulebook_pylint.checkers import Checker
from rulebook_pylint.internals import Messages

if TYPE_CHECKING:
    from pylint.lint import PyLinter


class BlockCommentLineTrimmingChecker(Checker):
    """See wiki: https://github.com/hendraanggrian/rulebook/wiki/Rules#block-comment-line-trimming
    """
    MSG_FIRST: str = 'block-comment-line-trimming-first'
    MSG_LAST: str = 'block-comment-line-trimming-last'

    name: str = 'block-comment-line-trimming'
    msgs: dict[str, MessageDefinitionTuple] = Messages.of(MSG_FIRST, MSG_LAST)

    def visit_module(self, node: Module) -> None:
        self._process(node.doc_node)

    def visit_classdef(self, node: ClassDef) -> None:
        self._process(node.doc_node)

    def visit_functiondef(self, node: FunctionDef) -> None:
        self._process(node.doc_node)

    def _process(self, docstring: Const | None) -> None:
        # first line of filter
        if not docstring or not isinstance(docstring, Const):
            return None

        # checks for violation
        if docstring.value.startswith('\n\n'):
            self.add_message(self.MSG_FIRST, node=docstring)
        if re.search(r'\n\n\s*$', docstring.value):
            self.add_message(self.MSG_LAST, node=docstring)
        return None


def register(linter: 'PyLinter') -> None:
    linter.register_checker(BlockCommentLineTrimmingChecker(linter))
