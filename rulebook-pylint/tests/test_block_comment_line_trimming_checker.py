from unittest import main

from astroid import extract_node
from pylint.testutils import CheckerTestCase
from rulebook_pylint.block_comment_line_trimming_checker \
    import BlockCommentLineTrimmingChecker

from .tests import msg


class TestBlockCommentLineTrimmingChecker(CheckerTestCase):
    CHECKER_CLASS = BlockCommentLineTrimmingChecker

    def test_summary_without_initial_and_final_newline(self):
        node1 = \
            extract_node(
                '''
                class Foo: #@
                    """
                    Lorem ipsum
                    """
                    print()
                ''',
            )
        with self.assertNoMessages():
            self.checker.visit_classdef(node1)

    def test_summary_with_initial_and_final_newline(self):
        node1 = \
            extract_node(
                '''
                class Foo: #@
                    """

                    Lorem ipsum

                    """
                    print()
                ''',
            )
        with self.assertAddsMessages(
            msg(BlockCommentLineTrimmingChecker.MSG_FIRST, (3, 4, 7, 7), node1.doc_node),
            msg(BlockCommentLineTrimmingChecker.MSG_LAST, (3, 4, 7, 7), node1.doc_node),
        ):
            self.checker.visit_classdef(node1)


if __name__ == '__main__':
    main()
