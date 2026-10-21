
- Reorganize
- Renaming Morphtool or items inside morphtool (before they were included) should be supported

### Aliases
Current behavior: Set `mekanismgenerators=mekanism` in the config and a mekanism item works with blocks from mekanism and mekanismgenerators

Planned behavior:
- Setting `mekanismgenerators=mekanism` allows using mekanism item on block from mekanism and mekanismgenerators but also allows using an item from mekanismgenerators on blocks from both mods.
- Define a list of names in the config that are equivalent. So instead of multiple lines of `mekanismgenerators=mekanism` it could be `mekanism, mekanismgenerators, mekanismtools`. All listed ids are aliases of each other.