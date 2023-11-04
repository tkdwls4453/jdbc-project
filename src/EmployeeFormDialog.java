import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeFormDialog extends JDialog {
    private JTextField fnameField;
    private JTextField minitField;
    private JTextField lnameField;
    private JTextField ssnField;
    private JTextField bdateField;
    private JTextField addressField;
    private JTextField sexField;
    private JTextField salaryField;
    private JTextField superSsnField;
    private JTextField dnoField;

    private Connection connection;
    private boolean employeeAdded = false;
    private String[] employeeData = new String[10];

    public EmployeeFormDialog(Frame owner) {
        super(owner, "새 직원 정보 입력", true);

        JPanel panel = new JPanel(new GridLayout(10, 2));

        fnameField = new JTextField();
        minitField = new JTextField();
        lnameField = new JTextField();
        ssnField = new JTextField();
        bdateField = new JTextField();
        addressField = new JTextField();
        sexField = new JTextField();
        salaryField = new JTextField();
        superSsnField = new JTextField();
        dnoField = new JTextField();

        panel.add(new JLabel("First Name:"));
        panel.add(fnameField);
        panel.add(new JLabel("Middle Initial:"));
        panel.add(minitField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lnameField);
        panel.add(new JLabel("SSN:"));
        panel.add(ssnField);
        panel.add(new JLabel("Birth Date:"));
        panel.add(bdateField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Sex:"));
        panel.add(sexField);
        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);
        panel.add(new JLabel("Super SSN:"));
        panel.add(superSsnField);
        panel.add(new JLabel("Department Number:"));
        panel.add(dnoField);

        JButton addButton = new JButton("추가");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (EmployeeDao.existSsn(connection, ssnField.getText())) {
                    JOptionPane.showMessageDialog(panel, "이미 존재하는 직원은 생성할 수 없습니");
                    return;
                }

                if (!EmployeeDao.existSsn(connection, superSsnField.getText())) {
                    JOptionPane.showMessageDialog(panel, "존재하는 직원을 상사로 지정할 수 없습니다");
                    return;
                }

                if (!EmployeeDao.existDno(connection, dnoField.getText())) {
                    JOptionPane.showMessageDialog(panel, "존재하지 않는 부서를 지정할 수 없습니다.");
                    return;
                }
                addEmployee();
            }
        });

        add(panel, BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    public String[] addEmployee() {
        System.out.println("EmployeeFormDialog.addEmployee");
        String ssn = ssnField.getText();

        if (ssn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "SSN을 입력해야 합니다.");
            return null;
        }

        employeeData[0] = fnameField.getText();
        employeeData[1] = minitField.getText();
        employeeData[2] = lnameField.getText();
        employeeData[3] = ssn;
        employeeData[4] = bdateField.getText();
        employeeData[5] = addressField.getText();
        employeeData[6] = sexField.getText();
        employeeData[7] = salaryField.getText();
        employeeData[8] = superSsnField.getText();
        employeeData[9] = dnoField.getText();



        EmployeeDao.createEmployee(connection, employeeData[0], employeeData[1], employeeData[2]
                , ssn, employeeData[4], employeeData[5], employeeData[6], Double.parseDouble(employeeData[7])
                , employeeData[8], employeeData[9]);
        dispose();
        return employeeData;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String[] getEmployeeData() {
        return employeeData;
    }
}