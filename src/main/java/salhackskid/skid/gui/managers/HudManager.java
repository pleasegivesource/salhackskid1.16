package salhackskid.skid.gui.managers;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import salhackskid.skid.SalHackSkid;
import salhackskid.skid.gui.hud.GuiHudEditor;
import salhackskid.skid.gui.hud.HudComponentItem;
import salhackskid.skid.module.Value;
import salhackskid.skid.module.ValueListeners;

public class HudManager
{
    public HudManager()
    {
    }

    public void Init()
    {
//        Add(new WatermarkComponent());
//        Add(new ArrayListComponent());
//        Add(new InventoryComponent());
//        Add(new TabGUIComponent());
//        Add(new NotificationComponent());
//        Add(new CoordsComponent());
//        Add(new SpeedComponent());
//        Add(new BiomeComponent());
//        Add(new TimeComponent());
//        Add(new TPSComponent());
//        Add(new FPSComponent());
//        Add(new DirectionComponent());
//        Add(new TooltipComponent());
//        Add(new ArmorComponent());
//        Add(new KeyStrokesComponent());
//        Add(new HoleInfoComponent());
//        Add(new PlayerCountComponent());
//        Add(new PlayerFrameComponent());
//        Add(new NearestEntityFrameComponent());
//        Add(new YawComponent());
//        Add(new TotemCountComponent());
//        Add(new PingComponent());
//        Add(new ChestCountComponent());
//        Add(new TrueDurabilityComponent());
//        Add(new StopwatchComponent());
//        Add(new PvPInfoComponent());
//        Add(new SchematicaMaterialInfoComponent());
//        Add(new PlayerRender());
//        Add(new NearestPlayerRender());
//        Add(new WelcomeComponent());
//        Add(new BedCountComponent());
//        Add(new ServerLaggingComponent());
//
//        /// MUST be last in list
//        Add(new SelectorMenuComponent());

        CanSave = false;

        Items.forEach(p_Item ->
        {
            p_Item.LoadSettings();
        });

        CanSave = true;
    }

    public ArrayList<HudComponentItem> Items = new ArrayList<HudComponentItem>();
    private boolean CanSave = false;

    public void Add(HudComponentItem p_Item)
    {
        try
        {
            for (Field field : p_Item.getClass().getDeclaredFields())
            {
                if (Value.class.isAssignableFrom(field.getType()))
                {
                    if (!field.isAccessible())
                    {
                        field.setAccessible(true);
                    }

                    final Value val = (Value) field.get(p_Item);

                    ValueListeners listener = new ValueListeners()
                    {
                        @Override
                        public void OnValueChange(Value p_Val)
                        {
                            ScheduleSave(p_Item);
                        }
                    };

                    val.Listener = listener;
                    p_Item.ValueList.add(val);
                }
            }
            Items.add(p_Item);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void OnRender(float p_PartialTicks)
    {
        Screen l_CurrScreen = MinecraftClient.getInstance().currentScreen;

        if (l_CurrScreen != null)
        {
            if (l_CurrScreen instanceof GuiHudEditor)
            {
                return;
            }
        }

        GL11.glPushMatrix();

        Items.forEach(p_Item ->
        {
            if (!p_Item.IsHidden() && !p_Item.HasFlag(HudComponentItem.OnlyVisibleInHudEditor))
            {
                try
                {
                    p_Item.render(0, 0, p_PartialTicks);
                }
                catch (Exception e)
                {
                    System.out.println(e.toString());
                }
            }
        });

        GL11.glPopMatrix();
    }

    public static HudManager Get()
    {
        return SalHackSkid.getHudManager();
    }

    public void ScheduleSave(HudComponentItem p_Item)
    {
        if (!CanSave)
            return;

        try
        {
            GsonBuilder builder = new GsonBuilder();

            Gson gson = builder.setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("SalHack/HUD/" + p_Item.GetDisplayName() + ".json"));
            Map<String, String> map = new HashMap<>();

            map.put("displayname", p_Item.GetDisplayName());
            map.put("visible", !p_Item.IsHidden() ? "true" : "false");
            map.put("PositionX", String.valueOf(p_Item.GetX()));
            map.put("PositionY", String.valueOf(p_Item.GetY()));
            map.put("ClampLevel", String.valueOf(p_Item.GetClampLevel()));
            map.put("ClampPositionX", String.valueOf(p_Item.GetX()));
            map.put("ClampPositionY", String.valueOf(p_Item.GetY()));
            map.put("Side", String.valueOf(p_Item.GetSide()));

            for (Value l_Val : p_Item.ValueList)
            {
                map.put(l_Val.getName().toString(), l_Val.getValue().toString());
            }

            gson.toJson(map, writer);
            writer.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
