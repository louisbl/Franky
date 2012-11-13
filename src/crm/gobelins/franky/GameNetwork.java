package crm.gobelins.franky;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class GameNetwork {

	public GameNetwork() {
		// TODO Auto-generated constructor stub
	}
}

class ServeurBluetooth extends Thread {
	private final BluetoothServerSocket blueServerSocket;

	public ServeurBluetooth() {
		// On utilise un objet temporaire qui sera assign� plus tard �
		// blueServerSocket car blueServerSocket est "final"
		BluetoothServerSocket tmp = null;
		BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
		try {
			// MON_UUID est l'UUID (comprenez identifiant serveur) de
			// l'application. Cette valeur est n�cessaire c�t� client �galement
			// !
			if (blueAdapter != null) {
				tmp = blueAdapter.listenUsingRfcommWithServiceRecord(
						GameConsts.NOM, GameConsts.MON_UUID);
			}
		} catch (IOException e) {
		}
		blueServerSocket = tmp;
	}

	public void run() {
		BluetoothSocket blueSocket = null;
		// On attend une erreur ou une connexion entrante
		while (true) {
			try {
				blueSocket = blueServerSocket.accept();
			} catch (IOException e) {
				break;
			}
			// Si une connexion est accept�e
			if (blueSocket != null) {
				// On fait ce qu'on veut de la connexion (dans un thread
				// s�par�), � vous de la cr�er
				manageConnectedSocket(blueSocket);
				try {
					blueServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private void manageConnectedSocket(BluetoothSocket blueSocket) {
		// TODO Auto-generated method stub

	}

	// On stoppe l'�coute des connexions et on tue le thread
	public void cancel() {
		try {
			blueServerSocket.close();
		} catch (IOException e) {
		}
	}
}

class ClientBluetooth extends Thread {
	private final BluetoothSocket blueSocket;
	private final BluetoothDevice blueDevice;

	public ClientBluetooth(BluetoothDevice device) {
		// On utilise un objet temporaire car blueSocket et blueDevice sont
		// "final"
		BluetoothSocket tmp = null;
		blueDevice = device;

		// On r�cup�re un objet BluetoothSocket gr�ce � l'objet BluetoothDevice
		try {
			// MON_UUID est l'UUID (comprenez identifiant serveur) de
			// l'application. Cette valeur est n�cessaire c�t� serveur �galement
			// !
			tmp = device.createRfcommSocketToServiceRecord(GameConsts.MON_UUID);
		} catch (IOException e) {
		}
		blueSocket = tmp;
	}

	public void run() {
		// On annule la d�couverte des p�riph�riques (inutile puisqu'on est en
		// train d'essayer de se connecter)
		BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
		blueAdapter.cancelDiscovery();

		try {
			// On se connecte. Cet appel est bloquant jusqu'� la r�ussite ou la
			// lev�e d'une erreur
			blueSocket.connect();
		} catch (IOException connectException) {
			// Impossible de se connecter, on ferme la socket et on tue le
			// thread
			try {
				blueSocket.close();
			} catch (IOException closeException) {
			}
			return;
		}

		// Utilisez la connexion (dans un thread s�par�) pour faire ce que vous
		// voulez
		manageConnectedSocket(blueSocket);
	}

	private void manageConnectedSocket(BluetoothSocket blueSocket2) {
		// TODO Auto-generated method stub

	}

	// Annule toute connexion en cours et tue le thread
	public void cancel() {
		try {
			blueSocket.close();
		} catch (IOException e) {
		}
	}
}
