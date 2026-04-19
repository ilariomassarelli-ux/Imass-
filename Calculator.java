import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Calculator extends JFrame {

    private final JTextField display = new JTextField("0");
    private double accumulator = 0;
    private String pendingOp = null;
    private boolean startNewNumber = true;

    public Calculator() {
        super("Calcolatrice");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("SansSerif", Font.BOLD, 32));
        display.setPreferredSize(new Dimension(280, 60));
        add(display, BorderLayout.NORTH);

        String[][] layout = {
            {"C", "±", "%", "/"},
            {"7", "8", "9", "*"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"0", ".", "=",  ""}
        };

        JPanel grid = new JPanel(new GridLayout(5, 4, 6, 6));
        for (String[] row : layout) {
            for (String label : row) {
                if (label.isEmpty()) { grid.add(new JLabel()); continue; }
                grid.add(makeButton(label));
            }
        }
        add(grid, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JButton makeButton(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("SansSerif", Font.PLAIN, 20));
        b.setFocusPainted(false);
        b.addActionListener((ActionEvent e) -> onButton(label));
        return b;
    }

    private void onButton(String label) {
        switch (label) {
            case "C":
                accumulator = 0;
                pendingOp = null;
                startNewNumber = true;
                display.setText("0");
                break;
            case "±":
                if (!display.getText().equals("0")) {
                    if (display.getText().startsWith("-"))
                        display.setText(display.getText().substring(1));
                    else
                        display.setText("-" + display.getText());
                }
                break;
            case "%":
                display.setText(format(parse() / 100.0));
                startNewNumber = true;
                break;
            case "+": case "-": case "*": case "/":
                applyPending();
                pendingOp = label;
                startNewNumber = true;
                break;
            case "=":
                applyPending();
                pendingOp = null;
                startNewNumber = true;
                break;
            case ".":
                if (startNewNumber) { display.setText("0."); startNewNumber = false; }
                else if (!display.getText().contains(".")) display.setText(display.getText() + ".");
                break;
            default: // cifre
                if (startNewNumber || display.getText().equals("0")) {
                    display.setText(label);
                    startNewNumber = false;
                } else {
                    display.setText(display.getText() + label);
                }
        }
    }

    private void applyPending() {
        double current = parse();
        if (pendingOp == null) {
            accumulator = current;
        } else {
            switch (pendingOp) {
                case "+": accumulator += current; break;
                case "-": accumulator -= current; break;
                case "*": accumulator *= current; break;
                case "/":
                    if (current == 0) { display.setText("Errore"); accumulator = 0; pendingOp = null; return; }
                    accumulator /= current; break;
            }
        }
        display.setText(format(accumulator));
    }

    private double parse() {
        try { return Double.parseDouble(display.getText()); }
        catch (NumberFormatException e) { return 0; }
    }

    private String format(double v) {
        if (v == (long) v) return Long.toString((long) v);
        return Double.toString(v);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
    }
}
