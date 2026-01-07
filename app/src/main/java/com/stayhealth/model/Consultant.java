package com.stayhealth.ui.consultant;

public class Consultant {

    private String id;        // Firestore doc id (uid)
    private String name;
    private String email;
    private String status;    // "active" / "past"
    private int age;
    private double weight;    // kg
    private double height;    // cm
    private String chronicIllness;
    private int kcal;         // günlük kcal hedefi

    public Consultant() {
        // Firestore için boş constructor gerekli
    }

    public Consultant(String id, String name, String email, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    // --- Getter / Setter ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getChronicIllness() { return chronicIllness; }
    public void setChronicIllness(String chronicIllness) { this.chronicIllness = chronicIllness; }

    public int getKcal() { return kcal; }
    public void setKcal(int kcal) { this.kcal = kcal; }
}
