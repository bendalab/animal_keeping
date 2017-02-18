package animalkeeping.model;

/**
 * Created by jan on 27.12.16.
 */
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class License {
    private Long id;
    private String name;
    private String number;
    private Date startDate;
    private Date endDate;
    private Set<Quota> quotas =
            new HashSet<>(0);
    public License() { }

    public License(String name, String number, Date startDate) {
        this.name = name;
        this.number = number;
        this.startDate = startDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<Quota> getQuotas() {
        return quotas;
    }

    public void setQuotas(Set<Quota> quota) {
        this.quotas = quota;
    }

    @Override
    public String toString() {
        return "License{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
