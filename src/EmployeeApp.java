import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeApp {

    private static final int BOOLEAN_COLUMN = 0;
    private Connection connection;
    private JFrame frame;
    private JComboBox<String> searchOptions;
    private JComboBox<String> sexOptions;
    private JComboBox<String> updateOptions;
    private JTextField conditionField;
    private JTextField updateField;
    private JCheckBox[] checkboxes;
    private DefaultTableModel tableModel;
    private JTable table;

    public EmployeeApp() {
        initializeUI();
        connectToDatabase();
        setupTable();
        setUpUpdateField();
        addTableModelListener();
    }



    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/company";
        String user = "sangjin";
        String password = "qwer1234";
        connection = EmployeeDao.getConnection(url, user, password);
    }

    private void initializeUI() {
        frame = new JFrame("Employee Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JPanel searchPanel = new JPanel();
        JLabel searchLabel = new JLabel("검색범위:");
        searchOptions = new JComboBox<>(new String[]{"전체", "이름(성)", "SSN", "주소", "성별", "연봉(이상)", "연봉(이하)", "상사 이름(성)", "부서"});
        sexOptions = new JComboBox<>(new String[]{"M", "W"});
        conditionField = new JTextField(10);

        searchPanel.add(searchLabel);
        searchPanel.add(searchOptions);

        JPanel filterPanel = new JPanel();
        searchOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("EmployeeApp.actionPerformed");
                String selectedOption = (String) searchOptions.getSelectedItem();
                if (selectedOption.equals("성별")) {
                    filterPanel.removeAll();
                    filterPanel.add(sexOptions);
                } else if (selectedOption.equals("전체")) {
                    filterPanel.removeAll();
                } else {
                    filterPanel.removeAll();
                    filterPanel.add(conditionField);
                }
                filterPanel.revalidate();
                filterPanel.repaint();
            }
        });

        JPanel checkboxesPanel = new JPanel();
        checkboxesPanel.setLayout(new GridLayout(0, 3));
        checkboxes = new JCheckBox[]{
                new JCheckBox("Name"),
                new JCheckBox("ssn"),
                new JCheckBox("Bdate"),
                new JCheckBox("address"),
                new JCheckBox("sex"),
                new JCheckBox("salary"),
                new JCheckBox("supervisor"),
                new JCheckBox("Dname")
        };
        for (JCheckBox checkbox : checkboxes) {
            checkbox.setSelected(true);
            checkboxesPanel.add(checkbox);
        }

        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        buttonPanel.add(searchButton);

        JButton addButton = new JButton("추가");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewEmployee();
            }
        });

        frame.add(searchPanel);
        frame.add(filterPanel);
        frame.add(checkboxesPanel);
        frame.add(buttonPanel);
        frame.add(addButton);
        frame.setSize(1000, 600);
        frame.setVisible(true);
    }

    private void setUpUpdateField() {

        JPanel updatePanel = new JPanel();
        JLabel updateLabel = new JLabel("수정 속성:");

        updateOptions = new JComboBox<>(new String[]{"주소", "연봉", "상사 SSN", "부서 번호"});
        updateField = new JTextField(10);

        HashMap<String, String> updateAtt = new HashMap<>();
        updateAtt.put("주소", "Address");
        updateAtt.put("연봉", "Salary");
        updateAtt.put("상사 SSN", "Super_ssn");
        updateAtt.put("부서 번호", "Dno");

        JButton updateButton = new JButton("수정");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String updateAttribute = updateAtt.get(updateOptions.getSelectedItem()).toString();

                if (updateAttribute.equals("Super_ssn")) {
                    if(! EmployeeDao.existSsn(connection, updateField.getText())){
                        JOptionPane.showMessageDialog(frame, "존재하지 않는 직원입니다.");
                        return;
                    }
                }

                if (updateAttribute.equals("Dno")) {
                    if (!EmployeeDao.existDno(connection, updateField.getText())) {
                        JOptionPane.showMessageDialog(frame, "존재하지 않는 부서입니다.");
                        return;
                    }
                }

                updateEmployee(connection, updateAttribute, updateField.getText());
            }
        });
        updatePanel.add(updateLabel);
        updatePanel.add(updateOptions);
        updatePanel.add(updateField);
        updatePanel.add(updateButton);
        frame.add(updatePanel);
    }

    private void updateEmployee(Connection connection, String updateAttribute, String updateValue) {
        int rowCount = tableModel.getRowCount();
        List<String> selectedIds = new ArrayList<>();
        for (int row = 0; row < rowCount; row++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(row, 0);

            if (isSelected) {
                String id = (String) tableModel.getValueAt(row, 2);
                selectedIds.add(id);
            }
        }

        if (!selectedIds.isEmpty()) {
            EmployeeDao.updateEmployee(connection, selectedIds, updateAttribute, updateValue);
        }else{
            JOptionPane.showMessageDialog(frame, "하나 이상의 레코드를 선택하세요.");
        }
    }



    private void performSearch() {
        tableModel.setRowCount(0);
        String selectedOption = (String) searchOptions.getSelectedItem();
        List<String> selectedColumns = new ArrayList<>();
        for (int i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].isSelected()) {
                selectedColumns.add(checkboxes[i].getText());
            }
        }

        tableModel.setColumnCount(0);

        tableModel.addColumn("선택");
        for (String column : selectedColumns) {
            tableModel.addColumn(column);
        }

        if (selectedColumns.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "하나 이상의 열을 선택하세요.");
            return;
        }

        //"전체", "이름(성)", "SSN", "주소", "성별", "연봉(이상)", "연봉(이하)", "상사 이름(성)", "부서"
        String[] searchCondition = {"ALL", "FNAME", "SSN", "ADDRESS", "SEX", "BIG_SALARY", "SMALL_SALARY", "SUPERVISOR_NAME", "DNAME"};
        HashMap<String, String> conditionCommandMap = new HashMap<>();
        conditionCommandMap.put("전체", "ALL");
        conditionCommandMap.put("이름(성)", "FNAME");
        conditionCommandMap.put("SSN", "SSN");
        conditionCommandMap.put("주소", "ADDRESS");
        conditionCommandMap.put("성별", "SEX");
        conditionCommandMap.put("연봉(이상)", "BIG_SALARY");
        conditionCommandMap.put("연봉(이하)", "SMALL_SALARY");
        conditionCommandMap.put("부서", "DNAME");

        String conditionValue = "";
        if (selectedOption.equals("전체")) {
            conditionValue = "";
        } else if (selectedOption.equals("성별")) {
            conditionValue = (String) sexOptions.getSelectedItem();
        } else {
            conditionValue = conditionField.getText();
        }

        List<EmployeeDto> searchedEmployeeList = EmployeeDao.findAllEmployee(connection, conditionCommandMap.get(selectedOption), conditionValue);
        System.out.println(searchedEmployeeList.size());
        //[Name, ssn, Bdate, address, sex, salary, supervisor, Dname]

        for (EmployeeDto employeeDto : searchedEmployeeList) {
            Object[] row = new Object[selectedColumns.size() + 1];
            row[0] = false;
            for (int i = 0; i < selectedColumns.size(); i++) {
                switch (selectedColumns.get(i)) {
                    case "Name":
                        row[i + 1] = employeeDto.getName();
                        break;
                    case "ssn":
                        row[i + 1] = employeeDto.getSsn();
                        break;
                    case "Bdate":
                        row[i + 1] = employeeDto.getBdate();
                        break;
                    case "address":
                        row[i + 1] = employeeDto.getAddress();
                        break;
                    case "sex":
                        row[i + 1] = employeeDto.getSex();
                        break;
                    case "salary":
                        row[i + 1] = employeeDto.getSalary();
                        break;
                    case "supervisor":
                        row[i + 1] = employeeDto.getSupervisorName();
                        break;
                    case "Dname":
                        row[i + 1] = employeeDto.getDname();
                        break;
                }
            }
            tableModel.addRow(row);
        }

    }

    private void addNewEmployee() {
        System.out.println("EmployeeApp.addNewEmployee");
        EmployeeFormDialog dialog = new EmployeeFormDialog(frame);
        dialog.setConnection(connection);
        dialog.setVisible(true); // 사용자 정의 다이얼로그를 표시

        String[] employeeData = dialog.getEmployeeData();
        tableModel.addRow(employeeData);
    }

    private void setupTable() {
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(800, 400));
        frame.add(tableScrollPane);

        JPanel deleteButtonPanel = new JPanel();
        JButton deleteButton = new JButton("선택한 행 삭제");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = tableModel.getRowCount();
                List<String> selectedIds = new ArrayList<>();
                for (int row = 0; row < rowCount; row++) {
                    Boolean isSelected = (Boolean) tableModel.getValueAt(row, 0);

                    if (isSelected) {
                        String id = (String) tableModel.getValueAt(row, 2);
                        selectedIds.add(id);
                    }
                }

                if (!selectedIds.isEmpty()) {
                    for (String id : selectedIds) {
                        for (int row = 0; row < rowCount; row++) {
                            String rowId = (String) tableModel.getValueAt(row, 2);
                            if (rowId == id) {
                                tableModel.removeRow(row);
                                rowCount--;
                                row--;
                            }
                        }
                    }
                    EmployeeDao.deleteEmployee(connection, selectedIds);
                }
            }
        });
        deleteButtonPanel.add(deleteButton);
        frame.add(deleteButtonPanel);
    }
    private void addTableModelListener() {
        JPanel selected = new JPanel();
        frame.add(selected);

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    StringBuilder text = new StringBuilder();

                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        Boolean isSelected = (Boolean) tableModel.getValueAt(row, BOOLEAN_COLUMN);

                        if (isSelected) {
                            String fname = (String) tableModel.getValueAt(row, 1); // 'fname' 열의 인덱스 (1)에 해당하는 값
                            if (text.length() > 0) {
                                text.append(", ");
                            }
                            text.append(fname);
                        }
                    }

                    System.out.println(text);

                    // JPanel에 텍스트를 추가하기 위해 JLabel을 생성하고 JPanel에 추가합니다.
                    JLabel label = new JLabel(text.toString());
                    selected.removeAll(); // 이전 내용을 지우고 새로운 내용을 추가합니다.
                    selected.add(label);
                    selected.revalidate(); // 갱신
                    selected.repaint(); // 다시 그리기
                }
            }
        });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeApp());
    }



}
