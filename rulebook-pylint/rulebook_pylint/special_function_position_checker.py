from typing import TYPE_CHECKING

from astroid import NodeNG, FunctionDef
from pylint.typing import MessageDefinitionTuple
from rulebook_pylint.checkers import Checker
from rulebook_pylint.internals import Messages, has_decorator

if TYPE_CHECKING:
    from pylint.lint import PyLinter


class SpecialFunctionPositionChecker(Checker):
    """See wiki: https://github.com/hendraanggrian/rulebook/wiki/Rules#special-function-position
    """
    MSG: str = 'special-function-position'

    OBJECT_OVERRIDDEN_FUNCTIONS: set[str] = \
        {
            '__str__',
            '__hash__',
            '__eq__',
            '__new__',
            '__del__',
            '__repr__',
            '__bytes__',
            '__format__',
            '__lt__',
            '__le__',
            '__ne__',
            '__gt__',
            '__ge__',
            '__bool__',
        }

    name: str = 'object-overridden-function-position'
    msgs: dict[str, MessageDefinitionTuple] = Messages.of(MSG)

    def visit_functiondef(self, node: FunctionDef) -> None:
        # first line of filter
        if node.name not in self.OBJECT_OVERRIDDEN_FUNCTIONS:
            return None

        current: NodeNG = node
        while current is not None:
            # checks for violation
            if isinstance(current, FunctionDef) and \
                not has_decorator(current, 'staticmethod') and \
                current.name not in self.OBJECT_OVERRIDDEN_FUNCTIONS:
                self.add_message(self.MSG, node=node, args=node.name)
                return None

            current = current.next_sibling()
        return None


def register(linter: 'PyLinter') -> None:
    linter.register_checker(SpecialFunctionPositionChecker(linter))
