package me.ht9.rose.feature.module.modules.client.togglemsg;

import me.ht9.rose.event.bus.annotation.SubscribeEvent;
import me.ht9.rose.event.events.ModuleEvent;
import me.ht9.rose.feature.module.Module;
import me.ht9.rose.feature.module.annotation.Description;
import me.ht9.rose.feature.module.modules.client.clickgui.ClickGUI;
import me.ht9.rose.feature.module.modules.client.hudeditor.HudEditor;

@Description("Shows a message in chat when you toggle a module.")
public final class ToggleMsg extends Module
{
    private static final ToggleMsg instance = new ToggleMsg();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onModule(ModuleEvent event)
    {
        if (event.module() instanceof ClickGUI || event.module() instanceof HudEditor) return;
        if (event.type() == ModuleEvent.Type.ENABLE)
        {
            mc.ingameGUI.addChatMessage("\u00a7aEnabled \u00a77" + event.module().name());
        }
        else if (event.type() == ModuleEvent.Type.DISABLE)
        {
            mc.ingameGUI.addChatMessage("\u00a7cDisabled \u00a77" + event.module().name());
        }
    }

    public static ToggleMsg instance()
    {
        return instance;
    }
}