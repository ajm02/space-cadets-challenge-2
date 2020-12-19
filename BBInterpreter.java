import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;


public class BBInterpreter {

    private HashMap<String,Integer> variables;

    public BBInterpreter() {
        variables = new HashMap<String,Integer>();
    }

    public String retrieveVariable(String statement) { // Retrieves the variable the statement applies to
        statement = statement + " ";
        int variableStartIndex;

        if (statement.contains("clear") || statement.contains("incr") || statement.contains("decr")) {
            variableStartIndex = statement.indexOf("r") + 2;
        }

        else if (statement.contains("while")) {
            variableStartIndex = statement.indexOf("e") + 2;
        }

        else {
            return null;
        }

        int variableEndIndex = statement.indexOf(" ", variableStartIndex);
        String variable = statement.substring(variableStartIndex, variableEndIndex);
        return variable;
    }

    public void processStatements(String[] statements) {

        for (int i = 0; i < statements.length; i++) {
            String statement = statements[i];
            String variable = retrieveVariable(statement); // Retrieves the variable the statement applies to

            if (variable != null) {

                if (statement.contains("clear")) { // Sets variable to 0
                    variables.put(variable, 0);
                }

                if (statement.contains("incr")) { // Increments the value in the variable
                    int incrementedNumber = variables.get(variable) + 1;
                    variables.put(variable, incrementedNumber);
                }

                if (statement.contains("decr")) { // Decrements the value in the variable
                    int decrementedNumber = variables.get(variable) - 1;
                    variables.put(variable, decrementedNumber);
                }

                // Executes code while the variable is not equal to 0
                if (statement.contains("while") && statement.contains("not 0 do")) {
                    int count = 1;

                    for (int j = i + 1; j <= statements.length; j++) {
                        if (statements[j].contains("while") && statement.contains("not 0 do")) {
                            count++;
                        }

                        if (statements[j].contains("end")) {
                            count--;
                        }

                        if (count == 0) { // If count = 0, then the correct end to the while loop has been found
                            String[] subStatements = Arrays.copyOfRange(statements, i + 1, j);

                            while (variables.get(variable) != 0) {
                                processStatements(subStatements);
                            }

                            i = j; // Moves past the statements in the while loop, ensuring their aren't executed again
                            break;
                        }
                    }
                }

                if (statement.contains("clear") || statement.contains("incr") || statement.contains("decr")) {

                    for (String key : variables.keySet()) {
                        System.out.println(key + ": " + variables.get(key));
                    }

                    System.out.println("");
                }
            }
        }
    }


    public void interpret(BufferedReader reader) throws IOException {
        ArrayList<String> statements = new ArrayList<String>();
        String line = reader.readLine();

        while (line != null) { // All statements in the file added to an ArrayList
            String[] commands = line.split(";");

            for (String command : commands) {
                statements.add(command);
            }

            line = reader.readLine();
        }

        String[] statementArray = statements.toArray(new String[statements.size()]);
        processStatements(statementArray);
    }

    public static void main(String[] args) {
        BBInterpreter interpreter = new BBInterpreter();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            interpreter.interpret(reader); // Executes the code given in the BB file
        }

        catch (Exception e) {
            System.out.println("There was an error with reading the file");
        }
    }
}
