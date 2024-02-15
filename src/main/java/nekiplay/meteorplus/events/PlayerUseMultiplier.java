package nekiplay.meteorplus.events;

public class PlayerUseMultiplier {
	private float _forward = 0.2f;
	private float _sideways = 0.2f;
	public PlayerUseMultiplier(float forward, float sideways) {
		this._forward = forward;
		this._sideways = sideways;
	}
	public void setForward(float forward) {
		_forward = forward;
	}
	public float getForward() {
		return _forward;
	}
	public void setSideways(float sideways) {
		_sideways = sideways;
	}
	public float getSideways() {
		return _sideways;
	}
}
