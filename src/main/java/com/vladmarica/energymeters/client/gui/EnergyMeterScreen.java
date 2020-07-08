package com.vladmarica.energymeters.client.gui;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.Util;
import com.vladmarica.energymeters.block.BlockEnergyMeter;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import com.vladmarica.energymeters.client.Sprites;
import com.vladmarica.energymeters.client.model.TextureLocations;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import com.vladmarica.energymeters.tile.config.EnumRedstoneControlState;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

@OnlyIn(Dist.CLIENT)
public class EnergyMeterScreen extends Screen implements IPressable {

  private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(
      EnergyMetersMod.MODID, "textures/gui/energymeter.png");

  private static final NumberFormat RATE_FORMAT = NumberFormat.getNumberInstance(Locale.US);
  private static final NumberFormat TOTAL_FORMAT = NumberFormat.getNumberInstance(Locale.US);
  static {
    RATE_FORMAT.setMinimumFractionDigits(1);
    RATE_FORMAT.setMaximumFractionDigits(1);
    TOTAL_FORMAT.setMaximumFractionDigits(0);
  }

  private static final int TEXTURE_WIDTH = 256;
  private static final int TEXTURE_HEIGHT = 148;
  private static final int COLOR_GREY = 4210752;
  private static final int COLOR_WHITE = 0xFFFFFF;

  private static final int CONFIG_BUTTONS_OFFSET_X = 168;
  private static final int CONFIG_BUTTONS_OFFSET_Y = 24;

  private GuiIconButton rateLimitButton;
  private GuiButtonEnergyAlias energyAliasButton;
  private GuiButtonConfigEnum<EnumRedstoneControlState> redstoneControlButton;
  private Button setRateLimitButton;

  private TileEntityEnergyMeterBase tile;
  private BiMap<RelativeBlockSide, Direction> sideToFaceMap;
  private Map<RelativeBlockSide, GuiButtonSideConfig> sideToButtonMap = new HashMap<>();

  private ResourceLocation sideTexture;
  private ResourceLocation inputTexture;
  private ResourceLocation outputTexture;
  private ResourceLocation screenTexture;

  private TextFieldWidget rateLimitTextField;
  private boolean isEditingLimit = false;

  public EnergyMeterScreen(TileEntityEnergyMeterBase tile) {
    super(new StringTextComponent("Energy Meter"));

    this.tile = tile;
    this.updateSideMapping();

    BlockState state = tile.getWorld().getBlockState(tile.getPos());
    MeterType type = ((BlockEnergyMeter) state.getBlock()).getMeterType();
    this.sideTexture = TextureLocations.getGuiResource(TextureLocations.getSideTexture(type));
    this.inputTexture = TextureLocations.getGuiResource(TextureLocations.getInputTexture(type));
    this.outputTexture = TextureLocations.getGuiResource(TextureLocations.getOutputTexture(type));
    this.screenTexture = TextureLocations.getGuiResource(TextureLocations.getScreenTexture(type));
  }

  private void updateSideMapping() {
    Direction screenFace = this.tile.getScreenSide();
    this.sideToFaceMap = HashBiMap.create();
    this.sideToFaceMap.put(RelativeBlockSide.TOP, Direction.UP);
    this.sideToFaceMap.put(RelativeBlockSide.BOTTOM, Direction.DOWN);
    this.sideToFaceMap.put(RelativeBlockSide.FRONT, screenFace);
    this.sideToFaceMap.put(RelativeBlockSide.LEFT, Util.getLeftFace(screenFace));
    this.sideToFaceMap.put(RelativeBlockSide.RIGHT, Util.getRightFace(screenFace));
    this.sideToFaceMap.put(RelativeBlockSide.BACK, Util.getBackFace(screenFace));
  }

  @Override
  protected void init() {
    super.init();


    int x = (this.width - TEXTURE_WIDTH) / 2;
    int y = (this.height - TEXTURE_HEIGHT) / 2;

    this.buttons.add(this.rateLimitButton = new GuiIconButton(
        x + CONFIG_BUTTONS_OFFSET_X,
        y + CONFIG_BUTTONS_OFFSET_Y,
        Sprites.SPECTRAL_ARROW, this));

    this.buttons.add(this.energyAliasButton = new GuiButtonEnergyAlias(
        x + CONFIG_BUTTONS_OFFSET_X + GuiIconButton.SIZE + 5,
        y + CONFIG_BUTTONS_OFFSET_Y,
        this.tile.getEnergyAlias(), this));

    this.buttons.add(this.redstoneControlButton = new GuiButtonConfigEnum<>(
        "Redstone Control",
        x + CONFIG_BUTTONS_OFFSET_X + (GuiIconButton.SIZE * 2) + 10,
        y + CONFIG_BUTTONS_OFFSET_Y,
        EnumRedstoneControlState.class,
        tile.getRedstoneControlState(), this));

    int startX = 195;
    int startY = 87;

    this.sideToButtonMap = new HashMap<>();
    this.sideToButtonMap.put(RelativeBlockSide.FRONT,
        new GuiButtonSideConfig(x + startX, y + startY, RelativeBlockSide.FRONT, screenTexture, true));
    this.sideToButtonMap.put(RelativeBlockSide.BACK,
        new GuiButtonSideConfig(x + startX + GuiIconButton.SIZE, y + startY + GuiIconButton.SIZE, RelativeBlockSide.BACK, screenTexture));
    this.sideToButtonMap.put(RelativeBlockSide.TOP,
        new GuiButtonSideConfig(x + startX, y + startY - GuiIconButton.SIZE, RelativeBlockSide.TOP, screenTexture));
    this.sideToButtonMap.put(RelativeBlockSide.BOTTOM,
        new GuiButtonSideConfig(x + startX, y + startY + GuiIconButton.SIZE, RelativeBlockSide.BOTTOM, screenTexture));
    this.sideToButtonMap.put(RelativeBlockSide.LEFT,
        new GuiButtonSideConfig(x + startX - GuiIconButton.SIZE, y + startY, RelativeBlockSide.LEFT, screenTexture));
    this.sideToButtonMap.put(RelativeBlockSide.RIGHT,
        new GuiButtonSideConfig(x + startX + GuiIconButton.SIZE, y + startY, RelativeBlockSide.RIGHT, screenTexture));

    this.updateConfigButtonTextures();

    this.rateLimitTextField = new TextFieldWidget(this.font, x + 18, y + 67, 70, 12, "");
    this.rateLimitTextField.setValidator(Util::isValidRateLimitString);
    this.rateLimitTextField.setVisible(false);
    this.buttons.add(this.setRateLimitButton = new GuiButtonExt(x + 117, y + 65, 25, 16, "Set", this));
    this.setRateLimitButton.visible = false;

    // Disable rate limit button if the energy type is not limitable
    if (!tile.getEnergyType().isLimitable()) {
      this.rateLimitButton.active = false;
    }
  }

  private void updateConfigButtonTextures() {
    for (GuiButtonSideConfig button: this.sideToButtonMap.values()) {
      button.setTexture(this.getTextureForSide(button.getSide()));
    }
  }

  private ResourceLocation getTextureForSide(RelativeBlockSide side) {
    if (side == RelativeBlockSide.FRONT) {
      return screenTexture;
    }

    Direction face = this.sideToFaceMap.get(side);
    if (face == tile.getInputSide()) {
      return inputTexture;
    } else if (face == tile.getOutputSide()) {
      return outputTexture;
    }

    return sideTexture;
  }

  @Override
  public void tick() {
    if (this.tile.getEnergyType().isLimitable()) {
      this.rateLimitTextField.tick();
      this.rateLimitTextField.setVisible(isEditingLimit);
      this.rateLimitButton.active = !isEditingLimit;
      this.setRateLimitButton.visible = isEditingLimit;
    }

    super.tick();
  }

  private String getStatusString() {
    if (!this.tile.isFullyConnected()) {
      return TextFormatting.GOLD + "Not Connected";
    }
    if (this.tile.isDisabled()) {
      return TextFormatting.RED + "Disabled";
    }
    return TextFormatting.GREEN + "Active";
  }

  private String getRateLimitString() {
    return this.tile.getRateLimit() == TileEntityEnergyMeterBase.UNLIMITED_RATE
        ? "Unlimited"
        : Integer.toString(this.tile.getRateLimit());
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();


    int x = (this.width - TEXTURE_WIDTH) / 2;
    int y = (this.height - TEXTURE_HEIGHT) / 2;

    this.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    this.blit(x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);

    int titleWidth = this.font.getStringWidth("Energy Meter");
    int titleX = (TEXTURE_WIDTH - titleWidth) / 2;
    this.font.drawString("Energy Meter", x + titleX, y + 7, COLOR_GREY);

    int leftPanelOffset = 18;
    int statYIncr = 28;
    int statY = y + 28;

    String units = this.tile.getEnergyAlias().getDisplayName();
    this.font.drawString(TextFormatting.GRAY + "Transfer Rate", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.font.drawString(RATE_FORMAT.format(tile.getTransferRate() / tile.getEnergyScale()) + " " + units + "/t", x  + leftPanelOffset, statY + 11, COLOR_WHITE);
    statY += statYIncr;

    this.font.drawString(TextFormatting.GRAY + "Transfer Rate Limit", x  + leftPanelOffset, statY, COLOR_WHITE);
    if (!this.isEditingLimit) {
      this.font.drawString(getRateLimitString() + " " + units + "/t", x + leftPanelOffset, statY + 11, COLOR_WHITE);
    }
    statY += statYIncr;

    this.font.drawString(TextFormatting.GRAY + "Total Transferred", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.font.drawString(TOTAL_FORMAT.format(tile.getTotalEnergyTransferred() / tile.getEnergyScale()) + " " + units, x  + leftPanelOffset, statY + 11, COLOR_WHITE);
    statY += statYIncr;

    this.font.drawString(TextFormatting.GRAY + "Status", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.font.drawString(getStatusString(), x  + leftPanelOffset, statY + 11, COLOR_WHITE);

    this.updateConfigButtonTextures();
    for (GuiButtonSideConfig sideConfigButton : this.sideToButtonMap.values()) {
      GlStateManager.color4f(1, 1, 1, 1);
      sideConfigButton.render(mouseX, mouseY, partialTicks);
    }

    this.rateLimitTextField.render(mouseX, mouseY, partialTicks);
    if (this.isEditingLimit) {
      this.font.drawString(
          this.energyAliasButton.getAlias().getDisplayName() + "/t",
          rateLimitTextField.x + rateLimitTextField.getWidth() + 4,
          rateLimitTextField.y + 2,
          COLOR_WHITE);
    }

    super.render(mouseX, mouseY, partialTicks);

    for (GuiButtonSideConfig sideConfigButton : this.sideToButtonMap.values()) {
      if (sideConfigButton.isMouseHovered()) {
        List<String> lines = new ArrayList<>(1);
        lines.add(sideConfigButton.getSide().getLabel());
        if (sideConfigButton.getSide() == RelativeBlockSide.FRONT) {
          lines.add(TextFormatting.GRAY + "Screen");
        }
        if (this.sideToFaceMap.get(sideConfigButton.getSide()) == tile.getInputSide()) {
          lines.add(TextFormatting.GRAY + "Input");
        }
        if (this.sideToFaceMap.get(sideConfigButton.getSide()) == tile.getOutputSide()) {
          lines.add(TextFormatting.GRAY + "Output");
        }
        this.renderTooltip(lines, mouseX, mouseY);
        break;
      }
    }

    for (Widget button : this.buttons) {
      if (button instanceof IHasTooltip && button.isMouseOver(mouseX, mouseY)) {
        this.renderTooltip(((IHasTooltip) button).getTooltipLines(), mouseX, mouseY);
      }
    }

    if (this.rateLimitButton.isMouseOver(mouseX, mouseY)) {
      List<String> lines = new ArrayList<>();
      lines.add("Transfer Rate Limit");
      lines.add(TextFormatting.GRAY + getRateLimitString() + " " + units + "/t");

      if (!this.tile.getEnergyType().isLimitable()) {
        lines.add(TextFormatting.RED + this.tile.getEnergyType().getName() + " meters cannot be limited");
      }

      this.renderTooltip(lines,  mouseX, mouseY);
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    boolean result = this.rateLimitTextField.keyPressed(keyCode, scanCode, modifiers);
    return result || super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void onPress(Button button) {

  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }
}
