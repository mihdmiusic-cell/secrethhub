-- ============================================================
-- WRATH | DUELS  v1.1  (Black & White Edition - Fully Rounded)
-- ============================================================

local Players         = game:GetService("Players")
local RunService      = game:GetService("RunService")
local UIS             = game:GetService("UserInputService")
local TweenService    = game:GetService("TweenService")
local HttpService     = game:GetService("HttpService")
local ContentProvider = game:GetService("ContentProvider")
local Stats           = game:GetService("Stats")
local LP              = Players.LocalPlayer

local _isfile   = isfile   or (syn and syn.isfile)   or (getgenv and getgenv().isfile)   or function() return false end
local _readfile = readfile  or (syn and syn.readfile)  or (getgenv and getgenv().readfile)  or function() return nil  end
local _writefile= writefile or (syn and syn.writefile) or (getgenv and getgenv().writefile) or function() end

-- ============================================================
-- STATE
-- ============================================================
local State = {
    normalSpeed=60, carrySpeed=30, laggerSpeed=10.1,
    speedToggled=false, laggerEnabled=false,
    infJumpEnabled=false, antiRagdollEnabled=false, fpsBoostEnabled=false,
    guiVisible=true, uiLocked=false,
    isStealing=false, stealStartTime=nil, lastStealTick=0,
    autoLeftEnabled=false, autoRightEnabled=false,
    autoLeftPhase=1, autoRightPhase=1,
    dropEnabled=false, _tpInProgress=false,
    lastMoveDir=Vector3.new(0,0,0),
    unwalkEnabled=false,
    _prevCarry=30, _prevSpeed=false,
}

local Keys = {
    speed=Enum.KeyCode.Q, guiHide=Enum.KeyCode.LeftControl,
    autoLeft=Enum.KeyCode.L, autoRight=Enum.KeyCode.R,
    lagger=Enum.KeyCode.Unknown, tpDown=Enum.KeyCode.Unknown,
    drop=Enum.KeyCode.H,
}

-- ============================================================
-- Steal Config
-- ============================================================
local Steal = {
    AutoStealEnabled=false, StealRadius=20, StealDuration=0.25,
    Data={}, plotCache={}, plotCacheTime={}, cachedPrompts={}, promptCacheTime=0,
}

-- ============================================================
-- PRESETS
-- ============================================================
local Presets = {}
local PRESET_FILE = "WrathPresets.json"
local LAST_PRESET_FILE = "WrathLastPreset.json"
local CONFIG_FILE = "WrathConfig.json"

local function buildPresetSnapshot()
    return {
        normalSpeed   = State.normalSpeed,
        carrySpeed    = State.carrySpeed,
        laggerSpeed   = State.laggerSpeed,
        stealRadius   = Steal.StealRadius,
        stealDuration = Steal.StealDuration,
        infJump       = State.infJumpEnabled,
        antiRagdoll   = State.antiRagdollEnabled,
        fpsBoost      = State.fpsBoostEnabled,
        autoSteal     = Steal.AutoStealEnabled,
    }
end

local function savePresetsFile()
    local ok,encoded=pcall(function() return HttpService:JSONEncode(Presets) end)
    if ok then pcall(function() _writefile(PRESET_FILE,encoded) end) end
end

local function loadPresetsFile()
    local hasFile=false; pcall(function() hasFile=_isfile(PRESET_FILE) end)
    if not hasFile then return end
    local raw; pcall(function() raw=_readfile(PRESET_FILE) end)
    if not raw then return end
    local ok,decoded=pcall(function() return HttpService:JSONDecode(raw) end)
    if ok and decoded then Presets=decoded end
end
