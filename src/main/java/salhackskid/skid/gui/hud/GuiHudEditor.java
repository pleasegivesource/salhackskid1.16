package salhackskid.skid.gui.hud;

import java.io.IOException;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import salhackskid.skid.gui.SalGuiScreen;
import salhackskid.skid.gui.managers.HudManager;
import salhackskid.skid.utils.Render;

public class GuiHudEditor extends SalGuiScreen
{
    public GuiHudEditor(HudEditorModule p_HudEditor)
    {
        super();

        HudEditor = p_HudEditor;
    }

    private HudEditorModule HudEditor;
    private boolean Clicked = false;
    private boolean Dragging = false;
    private int ClickMouseX = 0;
    private int ClickMouseY = 0;

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrix, mouseX, mouseY, partialTicks);

        GL11.glPushMatrix();

        HudComponentItem l_LastHovered = null;

        for (HudComponentItem l_Item : HudManager.Get().Items)
        {
            if (!l_Item.IsHidden() && l_Item.Render(mouseX, mouseY, partialTicks))
                l_LastHovered = l_Item;
        }

        if (l_LastHovered != null)
        {
            /// Add to the back of the list for rendering
            HudManager.Get().Items.remove(l_LastHovered);
            HudManager.Get().Items.add(l_LastHovered);
        }

        if (Clicked)
        {
            final float l_MouseX1 = Math.min(ClickMouseX, mouseX);
            final float l_MouseX2 = Math.max(ClickMouseX, mouseX);
            final float l_MouseY1 = Math.min(ClickMouseY, mouseY);
            final float l_MouseY2 = Math.max(ClickMouseY, mouseY);

            Render.drawRect(l_MouseX1, l_MouseY1, l_MouseX2, l_MouseY2, 0x56EC6, 205);

            HudManager.Get().Items.forEach(p_Item ->
            {
                if (!p_Item.IsHidden())
                {
                    if (p_Item.IsInArea(l_MouseX1, l_MouseX2, l_MouseY1, l_MouseY2))
                        p_Item.SetSelected(true);
                    else if (p_Item.IsSelected())
                        p_Item.SetSelected(false);
                }
            });
        }

        GL11.glPopMatrix();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (HudComponentItem l_Item : HudManager.Get().Items)
        {
            if (!l_Item.IsHidden())
            {
                if (l_Item.OnMouseClick(mouseX, mouseY, mouseButton))
                    return;
            }
        }

        Clicked = true;
        ClickMouseX = mouseX;
        ClickMouseY = mouseY;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        HudManager.Get().Items.forEach(p_Item ->
        {
            if (!p_Item.IsHidden())
            {
                p_Item.OnMouseRelease(mouseX, mouseY, state);

                if (p_Item.IsSelected())
                    p_Item.SetMultiSelectedDragging(true);
                else
                    p_Item.SetMultiSelectedDragging(false);
            }
        });

        Clicked = false;
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void onGuiClosed()
    {
        super.onClose();

        if (HudEditor.isEnabled())
            HudEditor.toggle();

        Clicked = false;
        Dragging = false;
        ClickMouseX = 0;
        ClickMouseY = 0;
    }
}
