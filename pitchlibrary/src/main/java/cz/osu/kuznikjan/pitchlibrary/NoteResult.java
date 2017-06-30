package cz.osu.kuznikjan.pitchlibrary;


import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import be.tarsos.dsp.pitch.PitchDetectionResult;

public class NoteResult{
    private double pitch = 0.00f; // pitch of the note in Hz
    private Octave octave = new Octave();
    private Note note = new Note();
    private double noteHz = 0.00f; // Hz value of current note
    private boolean isPitched = false;
    private double probability = 0.00f; // probability of the note in %
    private String noteFullName = "?"; // name of the closest note
    private String previousFullName = "?"; // name of the previous note
    private String nextFullName = "?"; // name of the next note
    @Ignore
    private static final int LAST_TONE_IN_OCTAVE = 12;
    @Ignore
    private static final int PENULTIMATE_TONE_IN_OCTAVE = 11;
    @Ignore
    private static final int FIRST_TONE_IN_OCTAVE = 0;
    @Ignore
    private static String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C"};

    public NoteResult(){

    }

    public NoteResult(PitchDetectionResult pitchDetectionResult, Octave octave, Note note) {
        setPitch(pitchDetectionResult.getPitch());
        setPitched(pitchDetectionResult.isPitched());
        setProbability(pitchDetectionResult.getProbability());
        setOctave(octave);
        setNote(note);
        setNoteHz(getOctave().getOctaveFrequencies()[getNote().getNoteIndex()]);
        switch(note.getNoteIndex()) {
            case FIRST_TONE_IN_OCTAVE:
                setNoteFullName(note.getNoteName() + Integer.toString(octave.getOctaveIndex()));
                setNextFullName(noteNames[note.getNoteIndex()+1] + Integer.toString(octave.getOctaveIndex()));
                setPreviousFullName("B" + (octave.getOctaveIndex()-1));
                return;
            case PENULTIMATE_TONE_IN_OCTAVE:
                setPreviousFullName(noteNames[note.getNoteIndex()-1] + Integer.toString(octave.getOctaveIndex()));
                setNoteFullName(note.getNoteName() + Integer.toString(octave.getOctaveIndex()));
                setNextFullName("C" + (octave.getOctaveIndex()+1));
                return;
            case LAST_TONE_IN_OCTAVE:
                setPreviousFullName(noteNames[note.getNoteIndex()-1] + Integer.toString(octave.getOctaveIndex()));
                setNoteFullName(note.getNoteName() + Integer.toString(octave.getOctaveIndex()+1));
                setNextFullName("C#" + (octave.getOctaveIndex()+1));
                return;
        }
        setNoteFullName(note.getNoteName() + Integer.toString(octave.getOctaveIndex()));
        setNextFullName(noteNames[note.getNoteIndex()+1] + Integer.toString(octave.getOctaveIndex()));
        setPreviousFullName(noteNames[note.getNoteIndex()-1] + Integer.toString(octave.getOctaveIndex()));
    }

    public double getNoteHz() {
        return noteHz;
    }

    public void setNoteHz(double noteHz) {
        this.noteHz = noteHz;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public Octave getOctave() {
        return octave;
    }

    public void setOctave(Octave octave) {
        this.octave = octave;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public boolean isPitched() {
        return isPitched;
    }

    public void setPitched(boolean pitched) {
        isPitched = pitched;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability * 100;
    }

    public String getNoteFullName() {
        return noteFullName;
    }

    public void setNoteFullName(String noteFullName) {
        this.noteFullName = noteFullName;
    }

    public String getPreviousFullName() {
        return previousFullName;
    }

    public void setPreviousFullName(String previousFullName) {
        this.previousFullName = previousFullName;
    }

    public String getNextFullName() {
        return nextFullName;
    }

    public void setNextFullName(String nextFullName) {
        this.nextFullName = nextFullName;
    }

    @Override
    public String toString() {
        return "NoteResult{" +
                ", pitch=" + pitch +
                ", octave=" + octave +
                ", note=" + note +
                ", noteHz=" + noteHz +
                ", isPitched=" + isPitched +
                ", probability=" + probability +
                ", noteFullName='" + noteFullName + '\'' +
                ", previousFullName='" + previousFullName + '\'' +
                ", nextFullName='" + nextFullName + '\'' +
                '}';
    }
}
