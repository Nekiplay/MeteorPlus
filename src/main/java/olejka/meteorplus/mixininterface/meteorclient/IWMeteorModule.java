package olejka.meteorplus.mixininterface.meteorclient;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface IWMeteorModule {
	Module getModule();
	void setColor(Color color);

}
