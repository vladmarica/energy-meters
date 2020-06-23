package com.vladmarica.energymeters.client.gui;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.Util;
import com.vladmarica.energymeters.block.BlockEnergyMeter;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import com.vladmarica.energymeters.client.Sprites;
import com.vladmarica.energymeters.client.model.TextureLocations;
import com.vladmarica.energymeters.energy.EnergyType.EnergyAlias;
import com.vladmarica.energymeters.network.PacketUpdateMeterConfig;
import com.vladmarica.energymeters.network.PacketUpdateMeterSides;
import com.vladmarica.energymeters.network.PacketUpdateRateLimit;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import com.vladmarica.energymeters.tile.config.EnumRedstoneControlState;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnergyMeter extends GuiScreen {
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
  private GuiButton setRateLimitButton;

  private TileEntityEnergyMeterBase tile;
  private BiMap<RelativeBlockSide, EnumFacing> sideToFaceMap;
  private Map<RelativeBlockSide, GuiButtonSideConfig> sideToButtonMap = new HashMap<>();

  private ResourceLocation sideTexture;
  private ResourceLocation inputTexture;
  private ResourceLocation outputTexture;
  private ResourceLocation screenTexture;

  private GuiTextField rateLimitTextField;
  private boolean isEditingLimit = false;


  public GuiEnergyMeter(TileEntityEnergyMeterBase tile) {
    this.tile = tile;
    this.updateSideMapping();

    IBlockState state = tile.getWorld().getBlockState(tile.getPos());
    MeterType type = state.getValue(BlockEnergyMeter.PROP_TYPE);
    this.sideTexture = TextureLocations.getGuiResource(TextureLocations.getSideTexture(type));
    this.inputTexture = TextureLocations.getGuiResource(TextureLocations.getInputTexture(type));
    this.outputTexture = TextureLocations.getGuiResource(TextureLocations.getOutputTexture(type));
    this.screenTexture = TextureLocations.getGuiResource(TextureLocations.getScreenTexture(type));
  }

  private void updateSideMapping() {
    EnumFacing screenFace = this.tile.getScreenSide();
    this.sideToFaceMap = HashBiMap.create();
    this.sideToFaceMap.put(RelativeBlockSide.TOP, EnumFacing.UP);
    this.sideToFaceMap.put(RelativeBlockSide.BOTTOM, EnumFacing.DOWN);
    this.sideToFaceMap.put(RelativeBlockSide.FRONT, screenFace);
    this.sideToFaceMap.put(RelativeBlockSide.LEFT, Util.getLeftFace(screenFace));
    this.sideToFaceMap.put(RelativeBlockSide.RIGHT, Util.getRightFace(screenFace));
    this.sideToFaceMap.put(RelativeBlockSide.BACK, Util.getBackFace(screenFace));
  }

  @Override
  public void initGui() {
    super.initGui();

    int x = (this.width - TEXTURE_WIDTH) / 2;
    int y = (this.height - TEXTURE_HEIGHT) / 2;

    this.buttonList.add(this.rateLimitButton = new GuiIconButton(
        0,
        x + CONFIG_BUTTONS_OFFSET_X,
        y + CONFIG_BUTTONS_OFFSET_Y,
        Sprites.SPECTRAL_ARROW));

    this.buttonList.add(this.energyAliasButton = new GuiButtonEnergyAlias(
        1,
        x + CONFIG_BUTTONS_OFFSET_X + GuiIconButton.SIZE + 5,
        y + CONFIG_BUTTONS_OFFSET_Y,
        this.tile.getEnergyAlias()));

    this.buttonList.add(this.redstoneControlButton = new GuiButtonConfigEnum<>(
        2,
        "Redstone Control",
        x + CONFIG_BUTTONS_OFFSET_X + (GuiIconButton.SIZE * 2) + 10,
        y + CONFIG_BUTTONS_OFFSET_Y,
        EnumRedstoneControlState.class,
        tile.getRedstoneControlState()));

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

    this.rateLimitTextField = new GuiTextField(4, this.fontRenderer, x + 18, y + 67, 70, 12);
    this.rateLimitTextField.setValidator(Util::isValidRateLimitString);
    this.rateLimitTextField.setVisible(false);
    this.buttonList.add(this.setRateLimitButton = new GuiButtonExt(3, x + 117, y + 65, 25, 16, "Set"));
    this.setRateLimitButton.visible = false;

    // Disable rate limit button if the energy type is not limitable
    if (!tile.getEnergyType().isLimitable()) {
      this.rateLimitButton.enabled = false;
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

    EnumFacing face = this.sideToFaceMap.get(side);
    if (face == tile.getInputSide()) {
      return inputTexture;
    } else if (face == tile.getOutputSide()) {
      return outputTexture;
    }

    return sideTexture;
  }

  @Override
  public void updateScreen() {
    if (this.tile.getEnergyType().isLimitable()) {
      this.rateLimitTextField.updateCursorCounter();
      this.rateLimitTextField.setVisible(isEditingLimit);
      this.rateLimitButton.enabled = !isEditingLimit;
      this.setRateLimitButton.visible = isEditingLimit;
    }

    super.updateScreen();
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
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();

    int x = (this.width - TEXTURE_WIDTH) / 2;
    int y = (this.height - TEXTURE_HEIGHT) / 2;

    this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    this.drawTexturedModalRect(x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);

    int titleWidth = this.fontRenderer.getStringWidth("Energy Meter");
    int titleX = (TEXTURE_WIDTH - titleWidth) / 2;
    this.fontRenderer.drawString("Energy Meter", x + titleX, y + 7, COLOR_GREY);

    int leftPanelOffset = 18;
    int statYIncr = 28;
    int statY = y + 28;

    String units = this.tile.getEnergyAlias().getDisplayName();
    this.fontRenderer.drawString(TextFormatting.GRAY + "Transfer Rate", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.fontRenderer.drawString(RATE_FORMAT.format(tile.getTransferRate() / tile.getEnergyScale()) + " " + units + "/t", x  + leftPanelOffset, statY + 11, COLOR_WHITE);
    statY += statYIncr;

    this.fontRenderer.drawString(TextFormatting.GRAY + "Transfer Rate Limit", x  + leftPanelOffset, statY, COLOR_WHITE);
    if (!this.isEditingLimit) {
      this.fontRenderer.drawString(getRateLimitString() + " " + units + "/t", x + leftPanelOffset, statY + 11, COLOR_WHITE);
    }
    statY += statYIncr;

    this.fontRenderer.drawString(TextFormatting.GRAY + "Total Transferred", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.fontRenderer.drawString(TOTAL_FORMAT.format(tile.getTotalEnergyTransferred() / tile.getEnergyScale()) + " " + units, x  + leftPanelOffset, statY + 11, COLOR_WHITE);
    statY += statYIncr;

    this.fontRenderer.drawString(TextFormatting.GRAY + "Status", x  + leftPanelOffset, statY, COLOR_WHITE);
    this.fontRenderer.drawString(getStatusString(), x  + leftPanelOffset, statY + 11, COLOR_WHITE);

    this.updateConfigButtonTextures();
    for (GuiButtonSideConfig sideConfigButton : this.sideToButtonMap.values()) {
      GlStateManager.color(1, 1, 1, 1);
      sideConfigButton.draw(mouseX, mouseY);
    }

    this.rateLimitTextField.drawTextBox();
    if (this.isEditingLimit) {
      this.fontRenderer.drawString(
          this.energyAliasButton.getAlias().getDisplayName() + "/t",
          rateLimitTextField.x + rateLimitTextField.width + 4,
          rateLimitTextField.y + 2,
          COLOR_WHITE);
    }

    // Render buttons and labels
    super.drawScreen(mouseX, mouseY, partialTicks);

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
        this.drawHoveringText(lines, mouseX, mouseY);
        break;
      }
    }

    for (GuiButton button : this.buttonList) {
      if (button instanceof IHasTooltip && button.isMouseOver()) {
        this.drawHoveringText(((IHasTooltip) button).getTooltipLines(), mouseX, mouseY);
      }
    }

    if (this.rateLimitButton.isMouseOver()) {
      List<String> lines = new ArrayList<>();
      lines.add("Transfer Rate Limit");
      lines.add(TextFormatting.GRAY + getRateLimitString() + " " + units + "/t");

      if (!this.tile.getEnergyType().isLimitable()) {
        lines.add(TextFormatting.RED + this.tile.getEnergyType().getName() + " meters cannot be limited");
      }

      this.drawHoveringText(lines,  mouseX, mouseY);
    }
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    // Left click
    if (mouseButton == 0) {
      for (GuiButtonSideConfig button : this.sideToButtonMap.values()) {
        if (!button.isDisabled() && button.isMouseHovered()) {
          this.sideConfigButtonClicked(button);
          break;
        }
      }

      if (this.setRateLimitButton.isMouseOver() && this.setRateLimitButton.visible && this.tile.getEnergyType().isLimitable() && this.isEditingLimit) {
        String str = this.rateLimitTextField.getText();
        if (Util.isValidRateLimitString(str)) {
          int rateLimit;
          if (str.isEmpty()) {
            rateLimit = TileEntityEnergyMeterBase.UNLIMITED_RATE;
          } else {
            rateLimit = Integer.parseInt(str);
          }

          this.isEditingLimit = false;
          this.tile.setRateLimit(rateLimit);
          EnergyMetersMod.NETWORK.sendToServer(new PacketUpdateRateLimit(this.tile.getPos(), rateLimit));
        }
      }

      if (this.rateLimitButton.isMouseOver() && this.tile.getEnergyType().isLimitable() && !this.isEditingLimit) {
        this.isEditingLimit = true;
        this.rateLimitTextField.setText(
            this.tile.getRateLimit() == TileEntityEnergyMeterBase.UNLIMITED_RATE
                ? ""
                : Integer.toString(this.tile.getRateLimit()));
      }

      this.rateLimitTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (!this.rateLimitTextField.textboxKeyTyped(typedChar, keyCode))  {
      super.keyTyped(typedChar, keyCode);
    }
  }

  private void sideConfigButtonClicked(GuiButtonSideConfig button) {
    this.mc.getSoundHandler().playSound(
        PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));

    EnumFacing face = this.sideToFaceMap.get(button.getSide());
    if (face == this.tile.getInputSide()) {
      this.tile.setOutputSide(face);
      this.tile.setInputSide(null);
    } else if (face == this.tile.getOutputSide()) {
      this.tile.setOutputSide(null);
    } else {
      if (this.tile.getInputSide() != null && this.tile.getOutputSide() == null) {
        this.tile.setOutputSide(face);
      } else {
        this.tile.setInputSide(face);
      }
    }

    EnergyMetersMod.NETWORK.sendToServer(new PacketUpdateMeterSides(
        this.tile.getPos(),
        this.tile.getInputSide(),
        this.tile.getOutputSide()));
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    super.actionPerformed(button);

    boolean sendUpdatePacket = false;

    if (button == this.redstoneControlButton) {
      EnumRedstoneControlState newState = this.redstoneControlButton.cycle();
      this.tile.setRedstoneControlState(newState);
      sendUpdatePacket = true;
    }

    if (button == this.energyAliasButton) {
      EnergyAlias newAlias = this.energyAliasButton.cycle();
      this.tile.setEnergyAlias(newAlias);
      sendUpdatePacket = true;
    }

    if (sendUpdatePacket) {
      EnergyMetersMod.NETWORK.sendToServer(
          new PacketUpdateMeterConfig(
            this.tile.getPos(),
            this.tile.getRedstoneControlState(),
            this.tile.getEnergyAlias().getIndex()));
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }
}
