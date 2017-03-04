package cz.osu.kuznikjan.pitchdetector;

import be.tarsos.dsp.pitch.PitchDetectionResult;

public class NoteResult {
    private double pitch = 0.00f; // pitch of the note in Hz
    private Octave octave = new Octave();
    private Note note = new Note();
    private boolean isPitched = false;
    private double probability = 0.00f; // probability of the note in %
    private String noteFullName = "?"; // name of the closest note
    private static final int LAST_TONE_IN_OCTAVE = 12;

    public NoteResult(){

    }

    public NoteResult(PitchDetectionResult pitchDetectionResult, Octave octave, Note note) {
        if(note.getNoteIndex()==LAST_TONE_IN_OCTAVE){
            octave.increaseOctaveIndex();
        }

        setPitch(pitchDetectionResult.getPitch());
        setPitched(pitchDetectionResult.isPitched());
        setProbability(pitchDetectionResult.getProbability());
        setOctave(octave);
        setNote(note);
        setNoteFullName(note.getNoteName() + Integer.toString(octave.getOctaveIndex()));
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
}
