package lox;

abstract class Expr {
    static class Binary extends Expr {

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        private final Expr left;
        private final Expr right;
        private final Token operator;
    }
}
