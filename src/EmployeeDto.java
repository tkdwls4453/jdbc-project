public class EmployeeDto {
    private String name;
    private String ssn;
    private String bdate;
    private String address;
    private String sex;
    private String salary;
    private String supervisorName;
    private String dname;

    public EmployeeDto(String name, String ssn, String bdate, String address, String sex, String salary, String supervisorNmae, String dName) {
        this.name = name;
        this.bdate = bdate;
        this.address = address;
        this.sex = sex;
        this.salary = salary;
        this.supervisorName = supervisorNmae;
        this.dname = dName;
    }

    public String getName() {
        return name;
    }

    public String getSsn() {
        return ssn;
    }

    public String getBdate() {
        return bdate;
    }

    public String getAddress() {
        return address;
    }

    public String getSex() {
        return sex;
    }

    public String getSalary() {
        return salary;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public String getDname() {
        return dname;
    }

    @Override
    public String toString() {
        return "EmployeeDto{" +
                "name='" + name + '\'' +
                ", ssn='" + ssn + '\'' +
                ", bdate='" + bdate + '\'' +
                ", address='" + address + '\'' +
                ", sex='" + sex + '\'' +
                ", salary='" + salary + '\'' +
                ", supervisorName='" + supervisorName + '\'' +
                ", dname='" + dname + '\'' +
                '}';
    }
}
