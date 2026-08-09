package toughasnails.api.stat.capability;

/**
 * Legacy compatibility holder. Forge 1.7.10 stores player data through
 * IExtendedEntityProperties rather than capabilities.
 */
public class CapabilityProvider<C>
{
    private final C instance;
    
    public CapabilityProvider(C instance)
    {
        this.instance = instance;
    }
    
    public C getInstance()
    {
        return this.instance;
    }
}
