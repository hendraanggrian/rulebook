from typing import TYPE_CHECKING

from astroid import Const, Module, ClassDef, FunctionDef
from pylint.typing import MessageDefinitionTuple
from rulebook_pylint.checkers import Checker
from rulebook_pylint.internals import Messages

if TYPE_CHECKING:
    from pylint.lint import PyLinter

class EmptyBlockCommentLineJoiningChecker(Checker):
    """See wiki: https://github.com/hendraanggrian/rulebook/wiki/Rules#empty-block-comment-line-joining
    """
    MSG: str = 'empty-block-comment-line-joining'

    name: str = 'empty-block-comment-line-joining'
    msgs: dict[str, MessageDefinitionTuple] = Messages.get(MSG)

    def visit_module(self, node: Module) -> None:
        self._process(node.doc_node)

    def visit_classdef(self, node: ClassDef) -> None:
        self._process(node.doc_node)

    def visit_functiondef(self, node: FunctionDef) -> None:
        self._process(node.doc_node)

    def _process(self, docstring: Const | None) -> None:
        # checks for violation
        if not docstring or not isinstance(docstring, Const) or '\n\n\n' not in docstring.value:
            return
        self.add_message(EmptyBlockCommentLineJoiningChecker.MSG, node=docstring)

def register(linter: 'PyLinter') -> None:
    linter.register_checker(EmptyBlockCommentLineJoiningChecker(linter))