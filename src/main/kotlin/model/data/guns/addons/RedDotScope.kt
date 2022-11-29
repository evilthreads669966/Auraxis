package model.data.guns.addons

import model.data.guns.Gun

/*A gun addon that adds plus 1 to the damage*/
class RedDotScope(gun: Gun) : Gun by gun {
    override var dmg: Int = gun.dmg + 1
}
