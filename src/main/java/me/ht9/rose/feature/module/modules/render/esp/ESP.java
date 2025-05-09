package me.ht9.rose.feature.module.modules.render.esp;

import me.ht9.rose.event.bus.annotation.SubscribeEvent;
import me.ht9.rose.event.events.RenderWorldPassEvent;
import me.ht9.rose.feature.module.Module;
import me.ht9.rose.feature.module.annotation.Description;
import me.ht9.rose.feature.module.setting.Setting;
import me.ht9.rose.mixin.accessors.EntityRendererAccessor;
import me.ht9.rose.util.render.Framebuffer;
import me.ht9.rose.util.render.Shader;
import net.minecraft.src.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

@Description("Draws entities through walls")
public final class ESP extends Module
{
    private static final ESP instance = new ESP();

    private final Setting<Boolean> rainbow = new Setting<>("Rainbow", true);
    private final Setting<Boolean> fill = new Setting<>("Fill", true);
    private final Setting<Boolean> dotted = new Setting<>("Dotted", true, fill::value);

    private final Setting<Integer> red = new Setting<>("Red", 0, 30, 255, () -> !rainbow.value());
    private final Setting<Integer> green = new Setting<>("Green", 0, 159, 255, () -> !rainbow.value());
    private final Setting<Integer> blue = new Setting<>("Blue", 0, 78, 255, () -> !rainbow.value());

    private final Setting<Boolean> all = new Setting<>("All", true);
    private final Setting<Boolean> players = new Setting<>("Players", true, () -> !all.value());
    private final Setting<Boolean> animals = new Setting<>("Animals", true, () -> !all.value());
    private final Setting<Boolean> mobs = new Setting<>("Mobs", true, () -> !all.value());
    private final Setting<Boolean> items = new Setting<>("Items", true, () -> !all.value());

    private Shader shader;

    private int size = 0;

    private ESP()
    {
        setArrayListInfo(() -> String.valueOf(size));
    }

    @Override
    public void initGL() {
        shader = new Shader(
                "/assets/rose/shaders/vertex.vert",
                "/assets/rose/shaders/outline.frag",
                "resolution", "time", "red", "green", "blue", "rainbow", "fill", "dotted"
        );
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderWorldPass(RenderWorldPassEvent event)
    {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        ((EntityRendererAccessor) mc.entityRenderer).invokeRenderHand(event.partialTicks(), 2);

        glPushMatrix();
        glPushAttrib(GL_ENABLE_BIT | GL_LIGHTING_BIT);

        Framebuffer framebuffer = shader.framebuffer();
        //framebuffer.clearFramebuffer();
        framebuffer.bindFramebuffer(true);

        ((EntityRendererAccessor) mc.entityRenderer).invokeSetupCameraTransform(event.partialTicks(), 0);
        this.size = 0;
        for (Object object : mc.theWorld.loadedEntityList)
        {
            if (!(object instanceof Entity entity)) continue;
            if (entity.equals(mc.thePlayer)) continue;

            if (
                    all.value()
                    || (entity instanceof EntityPlayer && players.value())
                    || ((entity instanceof EntityAnimal || entity instanceof EntityWaterMob) && animals.value())
                    || ((entity instanceof EntityMob || entity instanceof EntityFlying) && mobs.value())
                    || (entity instanceof EntityItem && items.value())
            )
            {
                if (entity.ticksExisted == 0)
                {
                    entity.lastTickPosX = entity.posX;
                    entity.lastTickPosY = entity.posY;
                    entity.lastTickPosZ = entity.posZ;
                }

                glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                double var3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) event.partialTicks();
                double var5 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) event.partialTicks();
                double var7 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) event.partialTicks();
                float var9 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.partialTicks();
                float brightness = entity.getEntityBrightness(event.partialTicks());
                Render render = RenderManager.instance.getEntityRenderObject(entity);
                glColor3f(brightness, brightness, brightness);
                render.doRender(entity, var3 - RenderManager.renderPosX, var5 - RenderManager.renderPosY, var7 - RenderManager.renderPosZ, var9, event.partialTicks());
                this.size++;
            }
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        mc.entityRenderer.func_905_b();
        Framebuffer.framebuffer.bindFramebuffer(true);
        glUseProgram(shader.programId());

        ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        glUniform2f(shader.uniform("resolution"), (float) sr.getScaledWidth() * 2.5f, (float) sr.getScaledHeight() * 2.5f);
        glUniform1f(shader.uniform("time"), (((System.nanoTime() / 1000000F) * 3) % 1000000) / 5000.0f);

        glUniform1f(shader.uniform("red"), (float) red.value() / 255.0f);
        glUniform1f(shader.uniform("green"), (float) green.value() / 255.0f);
        glUniform1f(shader.uniform("blue"), (float) blue.value() / 255.0f);

        glUniform1i(shader.uniform("rainbow"), rainbow.value() ? 1 : 0);
        glUniform1i(shader.uniform("fill"), fill.value() ? 1 : 0);
        glUniform1i(shader.uniform("dotted"), dotted.value() ? 1 : 0);

        Shader.drawFramebuffer(framebuffer);
        glUseProgram(0);
        Framebuffer.framebuffer.bindFramebuffer(false);

        //glDisable(GL_LINE_SMOOTH);

        glDisable(GL_BLEND);

        glPopAttrib();
        glPopMatrix();
    }

    public static ESP instance()
    {
        return instance;
    }
}
