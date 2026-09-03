package ru.bamchik.modules;

import ru.bamchik.Module;

public class AutoClickerModule extends Module {
    public AutoClickerModule() { super("AutoClicker"); }
    private long last = 0;
    private static final int CPS = 20;
    private static final long DELAY = 1000 / CPS;

    @Override
    public void onTick() {
        if (mc.player == null) return;
        long now = System.currentTimeMillis();
        if (now - last >= DELAY) {
            mc.options.attackKey.setPressed(true);
            mc.options.attackKey.setPressed(false);
            last = now;
        }
    }
}