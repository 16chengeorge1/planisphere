package osu.planisphere.paxos;
import osu.planisphere.*;

public class Client extends Node{

	private int clientSequenceID = 0;
	private NodeIdentifier leaderID = new NodeIdentifier(Role.ACCEPTOR, 0);
	
	public Client(NodeIdentifier id, int timerInterval, Network network) {
		super(id, timerInterval, network);
		// TODO Auto-generated constructor stub
	}
	
	public void write(int key, int value){
		Request req = new Request(getID(), 1, key, value, clientSequenceID);
		clientSequenceID++;
		this.sendMessage(leaderID, req);
	}

	@Override
	public void handleTimer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}

	
}
