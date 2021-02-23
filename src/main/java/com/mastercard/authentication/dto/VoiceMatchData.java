package com.mastercard.authentication.dto;

public class VoiceMatchData<K> {

	private final K key;
	private final int likelihoodRatio;
	private final double distance;
	private final boolean isMatched;

	public VoiceMatchData(K key, int likelihoodRatio, double distance, boolean isMatched) {
		super();
		this.key = key;
		this.likelihoodRatio = likelihoodRatio;
		this.distance = distance;
		this.isMatched = isMatched;
	}

	public K getKey() {
		return key;
	}

	public int getLikelihoodRatio() {
		return likelihoodRatio;
	}

	public double getDistance() {
		return distance;
	}

	public boolean isMatched() {
		return isMatched;
	}

}
