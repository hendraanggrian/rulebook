public class MultilineControlFlowBracing {
    public void foo() {
        if (true)
            bar();
        else if (false) {
            baz();
        } else
            bar();
    }

    public void bar() {
    }

    public void baz() {
    }
}
