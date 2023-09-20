package showercurtain.civilizations;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CivsClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("civilizations");

	@Override
	public void onInitializeClient() {
		LOGGER.warn("This mod does not yet have any client side functionality");
	}
}