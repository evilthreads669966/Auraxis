package model.data.guns.addons

import model.data.guns.Gun

/*Gun addon that adds plus 5 to the damage*/
class HighVelocityAmmo(gun: Gun) : Gun by gun {
    override var dmg: Int = gun.dmg + 5
}
