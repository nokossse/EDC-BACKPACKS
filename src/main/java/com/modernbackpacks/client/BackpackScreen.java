package com.modernbackpacks.client;

import com.modernbackpacks.menu.BackpackMenu;
import com.modernbackpacks.network.ModNetwork;
import com.modernbackpacks.network.ToggleAutoHydratePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    private static final int PANEL_WIDTH = 34;
    private static final int PANEL_HEIGHT = 50;
    private static final int PANEL_TOP = 10;

    private final int containerRows;
    private Button autoHydrateButton;

    public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.containerRows = menu.getBackpackRows();
        this.imageWidth = 176;
        this.imageHeight = 114 + this.containerRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        if (this.menu.hasHydrationSlot()) {
            this.autoHydrateButton = this.addRenderableWidget(Button.builder(Component.empty(),
                            button -> ModNetwork.CHANNEL.sendToServer(new ToggleAutoHydratePacket()))
                    .bounds(this.leftPos - PANEL_WIDTH + 4, this.topPos + PANEL_TOP + 30, PANEL_WIDTH - 8, 14)
                    .build());
            updateAutoHydrateButton();
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.autoHydrateButton != null) {
            updateAutoHydrateButton();
        }
    }

    private void updateAutoHydrateButton() {
        boolean on = this.menu.isAutoHydrate();
        Component state = Component.translatable(on ? "gui.modern_backpacks.on" : "gui.modern_backpacks.off")
                .withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED);
        this.autoHydrateButton.setMessage(state);
        this.autoHydrateButton.active = this.menu.isWorn();
        this.autoHydrateButton.setTooltip(Tooltip.create(this.menu.isWorn()
                ? Component.translatable("gui.modern_backpacks.auto_hydrate", state)
                : Component.translatable("gui.modern_backpacks.auto_hydrate.worn_only")));
    }

    /** The side panel is outside the texture; without this, clicking its slot would drop the cursor item. */
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        boolean inPanel = mouseX >= left - PANEL_WIDTH && mouseX < left
                && (this.menu.hasHydrationSlot() && mouseY >= top + PANEL_TOP && mouseY < top + PANEL_TOP + PANEL_HEIGHT
                || this.menu.getWeaponSlotCount() > 0 && mouseY >= top + weaponPanelTop()
                && mouseY < top + weaponPanelTop() + weaponPanelHeight());
        return !inPanel && super.hasClickedOutside(mouseX, mouseY, left, top, button);
    }

    private int weaponPanelTop() {
        return BackpackMenu.weaponPanelTop(this.menu.hasHydrationSlot());
    }

    private int weaponPanelHeight() {
        return 16 + this.menu.getWeaponSlotCount() * 18;
    }

    private void renderFramedPanel(GuiGraphics graphics, int x, int panelTop, int height) {
        int left = x - PANEL_WIDTH;
        int top = panelTop;
        int right = x - 4;
        int bottom = top + height - 4;
        // 9-slice of the generic_54 frame (4px corners, 256x256 texture)
        graphics.blit(TEXTURE, left + 4, top + 4, PANEL_WIDTH - 8, height - 8, 8, 8, 1, 1, 256, 256);
        graphics.blit(TEXTURE, left + 4, top, PANEL_WIDTH - 8, 4, 8, 0, 1, 4, 256, 256);
        graphics.blit(TEXTURE, left + 4, bottom, PANEL_WIDTH - 8, 4, 8, 218, 1, 4, 256, 256);
        graphics.blit(TEXTURE, left, top + 4, 4, height - 8, 0, 8, 4, 1, 256, 256);
        graphics.blit(TEXTURE, right, top + 4, 4, height - 8, 172, 8, 4, 1, 256, 256);
        graphics.blit(TEXTURE, left, top, 0, 0, 4, 4);
        graphics.blit(TEXTURE, right, top, 172, 0, 4, 4);
        graphics.blit(TEXTURE, left, bottom, 0, 218, 4, 4);
        graphics.blit(TEXTURE, right, bottom, 172, 218, 4, 4);
    }

    private void renderSlotFrame(GuiGraphics graphics, int x, int y, int slotX, int slotY) {
        graphics.blit(TEXTURE, x + slotX - 1, y + slotY - 1, 7, 17, 18, 18);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (this.menu.hasHydrationSlot()) {
            renderFramedPanel(graphics, x, y + PANEL_TOP, PANEL_HEIGHT);
            renderSlotFrame(graphics, x, y, BackpackMenu.HYDRATION_SLOT_X, BackpackMenu.HYDRATION_SLOT_Y);
        }
        if (this.menu.getWeaponSlotCount() > 0) {
            renderFramedPanel(graphics, x, y + weaponPanelTop(), weaponPanelHeight());
            for (int i = 0; i < this.menu.getWeaponSlotCount(); i++) {
                renderSlotFrame(graphics, x, y, BackpackMenu.WEAPON_SLOT_X, weaponPanelTop() + 8 + i * 18);
            }
        }
        int slotAreaHeight = this.containerRows * 18 + 17;
        int maxTextureSlotHeight = 6 * 18 + 17;

        if (slotAreaHeight <= maxTextureSlotHeight) {
            graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, slotAreaHeight);
        } else {
            graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, maxTextureSlotHeight);
            graphics.blit(TEXTURE, x, y + maxTextureSlotHeight, 0, 107, this.imageWidth, slotAreaHeight - maxTextureSlotHeight);
        }
        graphics.blit(TEXTURE, x, y + slotAreaHeight, 0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
