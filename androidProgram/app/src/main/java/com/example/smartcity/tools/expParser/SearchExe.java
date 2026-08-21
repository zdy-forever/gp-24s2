package com.example.smartcity.tools.expParser;
/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class SearchExe {
    /**
     * Executes the search process based on the provided `FinalExp` object.
     * If the `FinalExp` object contains a string expression, it processes that expression.
     * If the `FinalExp` object has further nested expressions, it recursively executes them.
     *
     * @param finalExp The final expression object containing string expressions and potentially other final expressions.
     */
        public void execute(FinalExp finalExp) {
            if (finalExp != null) {
                executeStringExp(finalExp.stringExp);

                if (finalExp.finalExp != null) {
                    execute(finalExp.finalExp);
                }
            }
        }

    /**
     * Executes the search operation for a `StringExp` object.
     * The method checks the type of the expression contained within the `StringExp` and
     * executes it accordingly. If it's a `UsernameExp`, the `UsernameExp.execute()` method is called.
     * If it's an `AccountExp`, the `AccountExp.execute()` method is called.
     *
     * @param stringExp The string expression containing the `Exp` to be executed.
     */
        private void executeStringExp(StringExp stringExp) {
            Exp exp = stringExp.exp;
            if (exp instanceof UsernameExp) {
                UsernameExp usernameExp = (UsernameExp) exp;
                usernameExp.execute();
            } else if (exp instanceof AccountExp) {
                AccountExp accountExp = (AccountExp) exp;
                accountExp.execute();
            }
        }

}
