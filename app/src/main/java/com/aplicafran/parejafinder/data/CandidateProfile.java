package com.aplicafran.parejafinder.data;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "candidate_profiles",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class CandidateProfile {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String email;
    public String nombre;
    public int edad;
    public String ciudad;
    public String intereses;

    public CandidateProfile(String email, String nombre, int edad, String ciudad, String intereses) {
        this.email = email;
        this.nombre = nombre;
        this.edad = edad;
        this.ciudad = ciudad;
        this.intereses = intereses;
    }
}
