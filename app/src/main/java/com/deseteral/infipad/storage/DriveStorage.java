package com.deseteral.infipad.storage;

import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

class DriveStorage implements Storage {

    private final GoogleApiClient googleApiClient;
    private final DriveFolder rootFolder;

    public DriveStorage(GoogleApiClient googleApiClient, DriveFolder rootFolder) {
        this.googleApiClient = googleApiClient;
        this.rootFolder = rootFolder;
    }

    @Override
    public void saveNote(String name, final String content) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(name)
                .setMimeType("text/plain")
                .build();

        rootFolder.createFile(googleApiClient, changeSet, null)
                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                        driveFileResult
                                .getDriveFile()
                                .open(googleApiClient, DriveFile.MODE_READ_WRITE, null)
                                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                    @Override
                                    public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                                        DriveContents driveContents = driveContentsResult.getDriveContents();

                                        try {
                                            ParcelFileDescriptor parcelFileDescriptor = driveContents.getParcelFileDescriptor();
                                            FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor
                                                    .getFileDescriptor());
                                            Writer writer = new OutputStreamWriter(fileOutputStream);
                                            writer.write(content);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        driveContents.commit(googleApiClient)
                                    }
                                });
                    }
                });
    }

    @Override
    public String loadNoteContent(String name) {
        return null;
    }

    @Override
    public void getList(final OnListFetchedCallback callback) {
        Drive.DriveApi.requestSync(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                rootFolder.listChildren(googleApiClient).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        List<String> files = new ArrayList<>();
                        for (Metadata m : result.getMetadataBuffer()) {
                            files.add(m.getTitle());
                        }

                        result.release();
                        callback.onListFetched(files);
                    }
                });
            }
        });
    }

    @Override
    public void deleteNote(String name) {

    }
}
