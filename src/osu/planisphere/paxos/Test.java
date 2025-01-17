package osu.planisphere.paxos;

import osu.planisphere.Network;

public class Test {

	public static void main(String[] args) throws Exception{
		Network network = new Network();
		Client client = new Client(PaxosConfiguration.clients[0], 5000, network);
		
		Acceptor acceptor0 = new Acceptor(PaxosConfiguration.acceptors[0], 5000, network);
		Acceptor acceptor1 = new Acceptor(PaxosConfiguration.acceptors[1], 5000, network);
		Acceptor acceptor2 = new Acceptor(PaxosConfiguration.acceptors[2], 5000, network);
		
		Learner learner0 = new Learner(PaxosConfiguration.learners[0], 5000, network);
		Learner learner1 = new Learner(PaxosConfiguration.learners[1], 5000, network);
		Learner learner2 = new Learner(PaxosConfiguration.learners[2], 5000, network);
		client.start();
		acceptor0.start();
		acceptor1.start();
		acceptor2.start();
		learner0.start();
		learner1.start();
		learner2.start();
		
		client.write(1, 1);
		Thread.sleep(1000);
		Recorder.printAll();
		Recorder.checkConsistency();
	}
}
