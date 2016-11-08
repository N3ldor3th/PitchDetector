package cz.osu.kuznikjan.pitchdetector;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by kuznijan on 17.3.2016.
 */
class DataLayerThread extends Thread {
    String path;
    String message;
    GoogleApiClient googleClient;

    DataLayerThread(GoogleApiClient googleApiClient, String p, String msg) {
        googleClient = googleApiClient;
        path = p;
        message = msg;
    }

    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
            }
            else {
                Log.v("myTag", "ERROR: failed to send Message");
            }
        }
    }
}
