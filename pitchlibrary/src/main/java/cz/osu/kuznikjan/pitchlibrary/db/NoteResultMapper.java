package cz.osu.kuznikjan.pitchlibrary.db;

import cz.osu.kuznikjan.pitchlibrary.NoteResult;

/**
 * Created by kuznijan on 10-Apr-17.
 */

public class NoteResultMapper {
    public static NoteResultDB mapNoteResult(NoteResult noteResult){
        NoteResultDB noteResultDB = new NoteResultDB();

        noteResultDB.setNoteHz(noteResult.getNoteHz());
        noteResultDB.setNoteFullName(noteResult.getNoteFullName());
        noteResultDB.setDifferenceCents(noteResult.getNote().getDifferenceCents());
        noteResultDB.setOctaveIndex(noteResult.getOctave().getOctaveIndex());
        noteResultDB.setDifferenceHz(noteResult.getNote().getDifferenceHz());
        noteResultDB.setNoteIndex(noteResult.getNote().getNoteIndex());
        noteResultDB.setPitch(noteResult.getPitch());
        noteResultDB.setProbability(noteResult.getProbability());

        return noteResultDB;
    }
}
