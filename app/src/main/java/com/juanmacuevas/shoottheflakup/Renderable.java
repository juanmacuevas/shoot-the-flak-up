package com.juanmacuevas.shoottheflakup;

import android.graphics.Canvas;

public interface Renderable {
	public void draw(Canvas c);

	public void update(long elapsedTime);

}
