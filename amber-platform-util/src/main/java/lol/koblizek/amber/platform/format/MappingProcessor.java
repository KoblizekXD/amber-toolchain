package lol.koblizek.amber.platform.format;

import lol.koblizek.amber.platform.MappingProvider;

import java.io.File;
import java.nio.file.Path;

public interface MappingProcessor<P extends VendorSpecificVersionProvider.GameDataProvider> {
    P getVersionProvider();

    File getClientMappings();
    File getServerMappings();
    void merge(Path output);

    MappingProvider getMappingProvider();
}
