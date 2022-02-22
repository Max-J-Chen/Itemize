package receipts;

public class Users {

    private String name = "";
    private Double assignedPrice;
    private boolean isOwner = false;

    public Users(String name) {
        this.name = name;
    }

    public void setIsOwner() {
        this.isOwner = true;
    }

    public String getUserName() {
        return name;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setAssignedPrice(Double assPrice) {
        assignedPrice = assPrice;
    }

    public Double getAssignedPrice() {
        return assignedPrice;
    }

}
