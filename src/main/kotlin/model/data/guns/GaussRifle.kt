package model.data.guns

class GaussRifle : Gun {
    override var dmg = 25
    override var ammo = 160
        set(value) {
            if (value <= 160 && value >= 0)
                field = value
        }
}