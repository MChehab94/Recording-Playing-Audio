package mchehab.com.recordingplayingaudio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private String FILE_RECORDING;

    private Button buttonPlayRecording;
    private Button buttonRecord;

    private final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
    private final String AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO;
    private final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FILE_RECORDING = getExternalCacheDir().getAbsolutePath()+"/recorder.aac";

        buttonRecord = findViewById(R.id.buttonRecord);
        buttonPlayRecording = findViewById(R.id.buttonPlayRecording);

        setButtonRecordListener();
        setButtonPlayRecordingListener();
        enableDisableButtonPlayRecording();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults[0] == PERMISSION_GRANTED){
                record();
            }
        }
    }

    private void setButtonRecordListener(){
        buttonRecord.setOnClickListener(e -> {
            if(buttonRecord.getText().toString().equalsIgnoreCase(getString(R.string.record))){
                record();
            }else{
                stopRecording();
                enableDisableButtonPlayRecording();
                buttonRecord.setText(getString(R.string.record));
            }
        });
    }

    private void setButtonPlayRecordingListener(){
        buttonPlayRecording.setOnClickListener(e -> {
            if(buttonPlayRecording.getText().toString().equalsIgnoreCase(getString(R.string.playRecord))){
                playRecording();
                buttonPlayRecording.setText(getString(R.string.stopRecording));
            }else{
                stopPlayingRecording();
                buttonPlayRecording.setText(getString(R.string.playRecord));
            }
        });
    }

    private void enableDisableButtonPlayRecording(){
        buttonPlayRecording.setEnabled(doesRecordingExist());
    }

    private boolean doesRecordingExist(){
        File file = new File(FILE_RECORDING);
        return file.exists();
    }

    private boolean isPermissionGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission
                (AUDIO_PERMISSION) == PERMISSION_GRANTED;

    }

    private void requestAudioPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[]{AUDIO_PERMISSION}, PERMISSION_REQUEST_CODE);
        }
    }

    private void record(){
        if(!isPermissionGranted()){
            requestAudioPermission();
            return;
        }
        buttonRecord.setText(getString(R.string.stopRecording));
        buttonRecord.setText(getString(R.string.stopRecording));
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setOutputFile(FILE_RECORDING);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void stopRecording(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void playRecording(){
        mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource(FILE_RECORDING);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                buttonPlayRecording.setText(getString(R.string.playRecord));
            });
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    private void stopPlayingRecording(){
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}