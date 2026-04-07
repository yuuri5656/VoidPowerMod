package com.dfdyz.void_power.client.gui;

import com.dfdyz.void_power.client.gui.widget.HologramTerminalWidget;
import com.dfdyz.void_power.menu.HologramMenu;
import com.dfdyz.void_power.network.CP.CP_HologramRename;
import com.dfdyz.void_power.network.PacketManager;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class HologramGUI extends Screen implements MenuAccess<HologramMenu> {
    HologramTE te;
    HologramTerminalWidget htw;

    EditBox name_editor;
    Button set_name;

    float terminal_scale = 1;
    private double terminal_size_factor = 1.0d;
    private double lastDisplayScale = -1d;
    private int lastScreenWidth = -1;
    private int lastScreenHeight = -1;
    final HologramMenu menu;

    public HologramGUI(HologramMenu menu, Inventory inventory, Component p_97743_) {
        super(p_97743_);
        this.menu = menu;
        if(menu != null){
            te = menu.te;
        }
    }

    float GetScale(){
        float minScale = (float) HologramTE.MIN_DISPLAY_SCALE;
        float w = 0.5f,h = 0.25f;
        
        int wi = te.getWidth();
        int hi = te.getHeight();
        
        
        if (hi <= 64) h = 2;
        else if (hi <= 128) h = 1;
        else if (hi <= 256) h = 0.5f;

        if (wi <= 64) w = 4;
        else if (wi <= 128) w = 2;
        else if (wi <= 256) w = 1;

        float scale = (float) (Math.min(w, h) * terminal_size_factor);

        if (width > 0 && height > 0 && wi > 0 && hi > 0) {
            float fitW = (width - 8f) / wi;
            float fitH = (height - 8f) / hi;
            float fit = Math.max(minScale, Math.min(fitW, fitH));
            scale = Math.min(scale, fit);
        }

        return Math.max(minScale, scale);
    }

    HologramTerminalWidget getTerminal(){
        terminal_scale = GetScale();
        int wi = te.getWidth();
        int hi = te.getHeight();
        int w = (int) (wi * terminal_scale);
        int h = (int) (hi * terminal_scale);
        return new HologramTerminalWidget(te, (width - w) / 2, (height - h) / 2, w, h);
    }

    public void SetDisplayScale(double scale) {
        terminal_size_factor = Mth.clamp(scale, HologramTE.MIN_DISPLAY_SCALE, HologramTE.MAX_DISPLAY_SCALE);
        UpdateTerminalLayout();
    }

    private void UpdateTerminalLayout() {
        if (te == null || htw == null) {
            return;
        }

        terminal_scale = GetScale();
        int w = Math.max(1, Math.round(te.getWidth() * terminal_scale));
        int h = Math.max(1, Math.round(te.getHeight() * terminal_scale));
        htw.setX((width - w) / 2);
        htw.setY((height - h) / 2);
        htw.setHeight(h);
        htw.setWidth(w);
        htw.MarkTerminalResized();

        if (name_editor != null && set_name != null) {
            if (htw.getY() + 29 < set_name.getHeight()) {
                name_editor.setY(htw.getY() - 30);
                set_name.setY(htw.getY() - 31);
            }
            else {
                name_editor.setY(1);
                set_name.setY(1);
            }
        }

        lastScreenWidth = width;
        lastScreenHeight = height;
        lastDisplayScale = terminal_size_factor;
    }

    @Override
    protected void init() {
        super.init();
        if (te != null) {
            terminal_size_factor = te.getDisplayScale();
        }
        htw = addRenderableWidget(getTerminal());

        name_editor = addRenderableWidget(
                new EditBox(font, width / 2 - 128 - 20, 1, 254, 16,
                Component.literal("NAME"))
        );

        name_editor.setValue(te.name);

        set_name = addRenderableWidget(Button.builder(Component.literal("Set"), this::ChangeName)
                .pos(width / 2 + 128-20, 1)
                .size(40,18)
                .build());

        UpdateTerminalLayout();

        setInitialFocus(htw);
    }

    public void ChangeName(Button b){
        te.Rename(name_editor.getValue());
        PacketManager.sendToServer(new CP_HologramRename(te));
    }

    @Override
    public void tick() {
        super.tick();
        if(te == null || te.isRemoved()) {
            Minecraft.getInstance().setScreen(null);
            return;
        }
        double syncedDisplayScale = te.getDisplayScale();
        if (Math.abs(syncedDisplayScale - lastDisplayScale) > 0.000001d) {
            SetDisplayScale(syncedDisplayScale);
        }
        if(htw.ShouldResize() || width != lastScreenWidth || height != lastScreenHeight){
            UpdateTerminalLayout();
        }
        //htw.setFocused(true);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        UpdateTerminalLayout();
    }

    @Override
    protected void setInitialFocus(GuiEventListener guiEventListener) {
        super.setInitialFocus(guiEventListener);
    }

    @Override
    public @NotNull HologramMenu getMenu() {
        return menu;
    }
}
