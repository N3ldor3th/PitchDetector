package cz.osu.kuznikjan.pitchlibrary.db;

import com.orm.SugarRecord;

/**
 * Created by kuznijan on 10-Apr-17.
 */

public class NoteResultDB extends SugarRecord {
    private double pitch = 0.00f;
    private double noteHz = 0.00f;
    private double probability = 0.00f;
    private String noteFullName = "?";
    private int octaveIndex = -1;
    private int noteIndex = -1;
    private double differenceHz = 0.00f;
    private double differenceCents = 0.00f;

    public NoteResultDB() {

    }

    public NoteResultDB(double pitch, double noteHz, double probability, String noteFullName, int octaveIndex, int noteIndex, double differenceHz, double differenceCents) {
        this.pitch = pitch;
        this.noteHz = noteHz;
        this.probability = probability;
        this.noteFullName = noteFullName;
        this.octaveIndex = octaveIndex;
        this.noteIndex = noteIndex;
        this.differenceHz = differenceHz;
        this.differenceCents = differenceCents;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getNoteHz() {
        return noteHz;
    }

    public void setNoteHz(double noteHz) {
        this.noteHz = noteHz;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public String getNoteFullName() {
        return noteFullName;
    }

    public void setNoteFullName(String noteFullName) {
        this.noteFullName = noteFullName;
    }

    public int getOctaveIndex() {
        return octaveIndex;
    }

    public void setOctaveIndex(int octaveIndex) {
        this.octaveIndex = octaveIndex;
    }

    public int getNoteIndex() {
        return noteIndex;
    }

    public void setNoteIndex(int noteIndex) {
        this.noteIndex = noteIndex;
    }

    public double getDifferenceHz() {
        return differenceHz;
    }

    public void setDifferenceHz(double differenceHz) {
        this.differenceHz = differenceHz;
    }

    public double getDifferenceCents() {
        return differenceCents;
    }

    public void setDifferenceCents(double differenceCents) {
        this.differenceCents = differenceCents;
    }

    @Override
    public String toString() {
        return "NoteResultDB{" +
                "id=" + super.getId() +
                ",  pitch=" + pitch +
                ", noteHz=" + noteHz +
                ", probability=" + probability +
                ", noteFullName='" + noteFullName + '\'' +
                ", octaveIndex=" + octaveIndex +
                ", noteIndex=" + noteIndex +
                ", differenceHz=" + differenceHz +
                ", differenceCents=" + differenceCents +
                '}';
    }
}
