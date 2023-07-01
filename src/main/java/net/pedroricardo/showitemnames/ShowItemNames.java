package net.pedroricardo.showitemnames;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShowItemNames implements ModInitializer {
    public static final String MOD_ID = "showitemnames";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ShowItemNames initialized.");
    }
}
