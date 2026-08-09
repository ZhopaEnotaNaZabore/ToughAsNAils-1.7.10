package toughasnails.handler.temperature;

import static toughasnails.util.RenderUtils.drawTexturedModalRect;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.TANCapabilities;
import toughasnails.api.config.TemperatureOption;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureScale;
import toughasnails.api.temperature.TemperatureScale.TemperatureRange;
import toughasnails.api.config.GameplayOption;
import toughasnails.temperature.TemperatureHandler;

@SideOnly(Side.CLIENT)
public class TemperatureOverlayHandler
{
    public static final ResourceLocation OVERLAY = new ResourceLocation("toughasnails:textures/gui/overlay.png");
    public static final ResourceLocation ICE_VIGNETTE = new ResourceLocation("toughasnails:textures/gui/ice_vignette.png");
    public static final ResourceLocation FIRE_VIGNETTE = new ResourceLocation("toughasnails:textures/gui/fire_vignette.png");
    
    private final Random random = new Random();
    private final Minecraft minecraft = Minecraft.getMinecraft();
    
    private int updateCounter;
    private FlashType flashType = FlashType.INCREASE;
    private int flashCounter = -1;
    private int prevTemperatureLevel = -1;
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == Phase.END && !minecraft.isGamePaused())
        {
            updateCounter++;
        }
    }
    
    @SubscribeEvent
    public void onPostRenderOverlay(RenderGameOverlayEvent.Post event)
    {
        ScaledResolution resolution = event.resolution;
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        TemperatureHandler temperatureStats = (TemperatureHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(player, TANCapabilities.TEMPERATURE);
        Temperature temperature = temperatureStats.getTemperature();

        //When the update counter isn't incrementing, ensure the same numbers are produced (freezes moving gui elements)
        random.setSeed((long)(updateCounter * 312871));
        
        // ALL is emitted after the rest of the in-game HUD.  PORTAL is not emitted
        // consistently when the player is not standing in a portal on 1.7.10.
        if (event.type == ElementType.ALL && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE))
        {
            if (!player.capabilities.isCreativeMode) drawTemperatureVignettes(width, height, temperature);
        }
        else if (event.type == ElementType.EXPERIENCE && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE))
        {
            minecraft.getTextureManager().bindTexture(OVERLAY);

            if (minecraft.playerController.gameIsSurvivalOrAdventure())
            {
                drawTemperature(width, height, temperature);
            }
        }
    }
    
    private void drawTemperature(int width, int height, Temperature temperature)
    {
        int left = width / 2 - 8;
        int top = height - 52; 
        
        TemperatureRange temperatureRange = temperature.getRange();

        if (temperatureRange == TemperatureRange.ICY || temperatureRange == TemperatureRange.HOT)
        {
            float shakeDelta = temperatureRange == TemperatureRange.ICY ? temperature.getRangeDelta(true) : temperature.getRangeDelta(false);
            
            if ((updateCounter % 1) == 0)
            {
                top += (int)((random.nextInt(3) - 1) * Math.min(shakeDelta * 3F, 1.0));
                left += (int)((random.nextInt(3) - 1) * Math.min(shakeDelta * 1.5F, 1.0));
            }
        }
        
        int temperatureLevel = temperature.getRawValue();
        
        if (prevTemperatureLevel == -1) prevTemperatureLevel = temperatureLevel;
        
        if (temperatureLevel > prevTemperatureLevel)
        {
            flashCounter = updateCounter + 16;
            flashType = FlashType.INCREASE;
        }
        else if (temperatureLevel < prevTemperatureLevel)
        {
            flashCounter = updateCounter + 16;
            flashType = FlashType.DECREASE;
        }
        
        prevTemperatureLevel = temperatureLevel;
        
        TemperatureIcon temperatureIcon = getTemperatureIcon(temperatureLevel);
        int updateDelta = flashCounter - updateCounter;
        
        drawTexturedModalRect(left, top, 16 * (temperatureIcon.backgroundIndex), 0, 16, 16);
        drawTexturedModalRect(left, top, 16 * (temperatureIcon.foregroundIndex), 0, 16, 16);
        
        if (temperatureIcon == TemperatureIcon.BALL)
        {
            renderColouredBall(left, top, temperature, 0);
        }
        
        if (flashCounter > (long)updateCounter)
        {
            if (updateDelta > 6 && updateDelta / 3L % 2L == 1L)
            {
                drawTexturedModalRect(left, top, 16 * (temperatureIcon.backgroundIndex + flashType.backgroundShift), 0, 16, 16);
                drawTexturedModalRect(left, top, 16 * (temperatureIcon.foregroundIndex + flashType.foregroundShift), 0, 16, 16);
                
                if (temperatureIcon == TemperatureIcon.BALL)
                {
                    renderColouredBall(left, top, temperature, 2);
                }
            }
            
            GL11.glPushMatrix();
            
            if (flashType == FlashType.INCREASE)
            {
                GL11.glTranslatef(left + 16F, top + 16F, 0F);
                GL11.glRotatef(180F, 0F, 0F, 1F);
            }
            else
            {
                GL11.glTranslatef(left, top, 0F);
            }
            
            drawTexturedModalRect(0, 0, 16 * (16 - updateDelta), 16 * 15, 16, 16);
            GL11.glPopMatrix();
        }
    }
    
    private void renderColouredBall(int x, int y, Temperature temperature, int textureShift)
    {
        TemperatureRange temperatureRange = temperature.getRange();
        float changeDelta = temperatureRange == TemperatureRange.COOL ? temperature.getRangeDelta(true) : temperature.getRangeDelta(false);
        
        if (temperatureRange != TemperatureRange.MILD)
        {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, changeDelta);
            drawTexturedModalRect(x, y, 16 * ((temperatureRange == TemperatureRange.COOL ? 8 : 9) + textureShift), 16 * 1, 16, 16);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }
    
    private void drawTemperatureVignettes(int width, int height, Temperature temperature)
    {
        TemperatureRange temperatureRange = temperature.getRange();
        float opacityDelta;
        
        ResourceLocation vignetteLocation = null;
        
        if (temperatureRange == TemperatureRange.ICY)
        {
            // The reversed delta grows as the player approaches the extreme end.
            opacityDelta = temperature.getRangeDelta(true);
            vignetteLocation = ICE_VIGNETTE;
        }
        else if (temperatureRange == TemperatureRange.HOT)
        {
            opacityDelta = temperature.getRangeDelta(true);
            vignetteLocation = FIRE_VIGNETTE;
        }
        else return;
        
        if (vignetteLocation != null)
        {
            if (temperatureRange == TemperatureRange.HOT)
            {
                drawHeatTint(width, height, opacityDelta * 0.28F);
            }

            minecraft.getTextureManager().bindTexture(vignetteLocation);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, opacityDelta);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0.0D, height, -90.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(width, height, -90.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(width, 0.0D, -90.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
            tessellator.draw();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /** A subtle heat haze keeps the centre readable while making severe heat unmistakable. */
    private void drawHeatTint(int width, int height, float opacity)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 0.26F, 0.03F, opacity);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(0.0D, height, -91.0D);
        tessellator.addVertex(width, height, -91.0D);
        tessellator.addVertex(width, 0.0D, -91.0D);
        tessellator.addVertex(0.0D, 0.0D, -91.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    private static TemperatureIcon getTemperatureIcon(int scalePos)
    {
        if (scalePos < 0 || scalePos > TemperatureScale.getScaleTotal())
        {
            return null;
        }
        
        TemperatureIcon temperatureIcon = null;
        
        for (int index = 0; index < TemperatureIcon.values().length; index++)
        {
            temperatureIcon = TemperatureIcon.values()[index];
            
            if (TemperatureScale.isScalePosInRange(scalePos, temperatureIcon.startRange, temperatureIcon.endRange))
            {
                break;
            }
        }
        
        return temperatureIcon;
    }
    
    private static enum TemperatureIcon
    {
        SNOWFLAKE(0, 9, TemperatureRange.ICY),
        BALL(1, 10, TemperatureRange.COOL, TemperatureRange.WARM),
        FIRE(2, 11, TemperatureRange.HOT);
        
        public final int backgroundIndex;
        public final int foregroundIndex;
        public final TemperatureRange startRange;
        public final TemperatureRange endRange;
        
        private TemperatureIcon(int backgroundIndex, int foregroundIndex, TemperatureRange startRange, TemperatureRange endRange)
        {
            this.backgroundIndex = backgroundIndex;
            this.foregroundIndex = foregroundIndex;
            this.startRange = startRange;
            this.endRange = endRange;
        }
        
        private TemperatureIcon(int backgroundIndex, int foregroundIndex, TemperatureRange range)
        {
            this(backgroundIndex, foregroundIndex, range, range);
        }
    }
    
    private static enum FlashType
    {
        INCREASE(3, 3), 
        DECREASE(3, 3);
        
        public final int backgroundShift;
        public final int foregroundShift;
        
        private FlashType(int backgroundShift, int foregroundShift)
        {
            this.backgroundShift = backgroundShift;
            this.foregroundShift = foregroundShift;
        }
    }
}
