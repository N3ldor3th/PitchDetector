package cz.osu.kuznikjan.pitchlibrary;

import be.tarsos.dsp.pitch.PitchDetectionResult;

public class NotePitchHandler {
    private static final double PITCH_LOW_LIMIT = 25.0f;
    private static final double PITCH_HIGH_LIMIT = 4200.0f;

    public static NoteResult mapPitchToNoteResult(PitchDetectionResult pitchDetectionResult) {

        if ((pitchDetectionResult.getPitch() < PITCH_LOW_LIMIT) || (pitchDetectionResult.getPitch() > PITCH_HIGH_LIMIT)) {
            return new NoteResult();
        }

        final Octave octave = new Octave(pitchDetectionResult);
        final Note note = new Note(octave.getOctaveFrequencies(), pitchDetectionResult);
        final NoteResult noteResult = new NoteResult(pitchDetectionResult, octave, note);

        return noteResult;
    }
}
