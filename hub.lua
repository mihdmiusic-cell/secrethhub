repeat task.wait() until game:IsLoaded()
local Players,RunService,UIS,TS,Lighting,HS = game:GetService("Players"),game:GetService("RunService"),game:GetService("UserInputService"),game:GetService("TweenService"),game:GetService("Lighting"),game:GetService("HttpService")
local LP = Players.LocalPlayer

-- =============== INTRO CONFIG LOAD ===============
local INTRO_CONFIG_FILE = "vortex_config.json"
local introSkipEnabled = false

local function loadIntroSkipConfig()
    if isfile and isfile(INTRO_CONFIG_FILE) then
        local ok, data = pcall(function() return HS:JSONDecode(readfile(INTRO_CONFIG_FILE)) end)
        if ok and data and data.skipIntro ~= nil then
            introSkipEnabled = data.skipIntro
        end
    end
end
loadIntroSkipConfig()

-- =============== INTRO SOURCE ===============
local introSource = [[
-- uranium.cc intro | LocalScript standalone
local Players = game:GetService("Players")
local TweenService = game:GetService("TweenService")
local Lighting = game:GetService("Lighting")
local UserInputService = game:GetService("UserInputService")
local TextService = game:GetService("TextService")

local player = Players.LocalPlayer
local playerGui = player:WaitForChild("PlayerGui")

local screenGui = Instance.new("ScreenGui")
screenGui.Name = "UraniumIntro"
screenGui.IgnoreGuiInset = true
screenGui.ResetOnSpawn = false
screenGui.ZIndexBehavior = Enum.ZIndexBehavior.Sibling
screenGui.Parent = playerGui

local introSkipped = false
local skipConnection = nil
local introSound = nil

local SOUND_ID = 120267378058133
local SKIP_SECONDS = 10

-- ===== EFFETTI =====
local blur = Instance.new("BlurEffect")
blur.Size = 0
blur.Parent = Lighting

local overlay = Instance.new("Frame")
overlay.Size = UDim2.fromScale(1, 1)
overlay.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
overlay.BackgroundTransparency = 0.3
overlay.BorderSizePixel = 0
overlay.ZIndex = 1
overlay.Parent = screenGui

local darkOverlay = Instance.new("Frame")
darkOverlay.Size = UDim2.fromScale(1, 1)
darkOverlay.BackgroundColor3 = Color3.fromRGB(5, 5, 10)
darkOverlay.BackgroundTransparency = 0.5
darkOverlay.BorderSizePixel = 0
darkOverlay.ZIndex = 1
darkOverlay.Parent = screenGui

local vignette = Instance.new("ImageLabel")
vignette.Size = UDim2.fromScale(1, 1)
vignette.BackgroundTransparency = 1
vignette.Image = "rbxassetid://195611797"
vignette.ImageColor3 = Color3.fromRGB(0, 0, 0)
vignette.ImageTransparency = 0.35
vignette.ZIndex = 2
vignette.Parent = screenGui

local container = Instance.new("Frame")
container.Size = UDim2.new(0, 850, 0, 130)
container.AnchorPoint = Vector2.new(0.5, 0.5)
container.Position = UDim2.new(0.5, 0, 0.5, 0)
container.BackgroundTransparency = 1
container.BorderSizePixel = 0
container.ClipsDescendants = false
container.ZIndex = 5
container.Parent = screenGui

-- TESTO UNICO "URANIUM.CC"
local mainLabel = Instance.new("TextLabel")
mainLabel.Size = UDim2.new(1, 0, 1, 0)
mainLabel.AnchorPoint = Vector2.new(0.5, 0.5)
mainLabel.Position = UDim2.new(0.5, 0, 0.5, 0)
mainLabel.BackgroundTransparency = 1
mainLabel.Text = "URANIUM.CC"
mainLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
mainLabel.TextStrokeColor3 = Color3.fromRGB(0, 0, 0)
mainLabel.TextStrokeTransparency = 0
mainLabel.Font = Enum.Font.GothamBlack
mainLabel.TextSize = 88
mainLabel.TextXAlignment = Enum.TextXAlignment.Center
mainLabel.TextTransparency = 1
mainLabel.ZIndex = 5
mainLabel.Parent = container

-- Label separate per l'animazione iniziale
local leftLabel = Instance.new("TextLabel")
leftLabel.Size = UDim2.new(0, 400, 1, 0)
leftLabel.AnchorPoint = Vector2.new(1, 0.5)
leftLabel.Position = UDim2.new(0.5, -10, 0.5, 0)
leftLabel.BackgroundTransparency = 1
leftLabel.Text = "URANIUM"
leftLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
leftLabel.TextStrokeColor3 = Color3.fromRGB(0, 0, 0)
leftLabel.TextStrokeTransparency = 0
leftLabel.Font = Enum.Font.GothamBlack
leftLabel.TextSize = 88
leftLabel.TextXAlignment = Enum.TextXAlignment.Right
leftLabel.TextTransparency = 1
leftLabel.ZIndex = 5
leftLabel.Visible = true
leftLabel.Parent = container

local rightLabel = Instance.new("TextLabel")
rightLabel.Size = UDim2.new(0, 200, 1, 0)
rightLabel.AnchorPoint = Vector2.new(0, 0.5)
rightLabel.Position = UDim2.new(0.5, -10, 0.5, 0)
rightLabel.BackgroundTransparency = 1
rightLabel.Text = ".CC"
rightLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
rightLabel.TextStrokeColor3 = Color3.fromRGB(0, 0, 0)
rightLabel.TextStrokeTransparency = 0
rightLabel.Font = Enum.Font.GothamBlack
rightLabel.TextSize = 88
rightLabel.TextXAlignment = Enum.TextXAlignment.Left
rightLabel.TextTransparency = 1
rightLabel.ZIndex = 5
rightLabel.Visible = true
rightLabel.Parent = container

-- Fascio di luce verde
local lightBeam = Instance.new("Frame")
lightBeam.Size = UDim2.new(0, 0, 1.1, 0)
lightBeam.AnchorPoint = Vector2.new(0, 0.5)
lightBeam.Position = UDim2.new(0, -20, 0.5, 0)
lightBeam.BackgroundColor3 = Color3.fromRGB(0, 255, 180)
lightBeam.BackgroundTransparency = 0.5
lightBeam.BorderSizePixel = 0
lightBeam.ZIndex = 6
lightBeam.Parent = container

local beamGlow = Instance.new("Frame")
beamGlow.Size = UDim2.new(0, 0, 1.3, 0)
beamGlow.AnchorPoint = Vector2.new(0, 0.5)
beamGlow.Position = UDim2.new(0, -20, 0.5, 0)
beamGlow.BackgroundColor3 = Color3.fromRGB(0, 255, 100)
beamGlow.BackgroundTransparency = 0.7
beamGlow.BorderSizePixel = 0
beamGlow.ZIndex = 5
beamGlow.Parent = container

local beamSparkles = Instance.new("Frame")
beamSparkles.Size = UDim2.new(0, 0, 1, 0)
beamSparkles.AnchorPoint = Vector2.new(0, 0.5)
beamSparkles.Position = UDim2.new(0, -20, 0.5, 0)
beamSparkles.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
beamSparkles.BackgroundTransparency = 0.8
beamSparkles.BorderSizePixel = 0
beamSparkles.ZIndex = 7
beamSparkles.Parent = container

-- Sottotitoli
local subLabel = Instance.new("TextLabel")
subLabel.Size = UDim2.new(1, 0, 0, 40)
subLabel.AnchorPoint = Vector2.new(0.5, 0)
subLabel.Position = UDim2.new(0.5, 0, 0.5, 70)
subLabel.BackgroundTransparency = 1
subLabel.Text = "THE ONE THAT MOGS ALL. . ."
subLabel.TextColor3 = Color3.fromRGB(0, 255, 180)
subLabel.Font = Enum.Font.GothamBold
subLabel.TextSize = 14
subLabel.TextXAlignment = Enum.TextXAlignment.Center
subLabel.TextTransparency = 1
subLabel.ZIndex = 5
subLabel.Parent = screenGui

local subLabel2 = Instance.new("TextLabel")
subLabel2.Size = UDim2.new(1, 0, 0, 25)
subLabel2.AnchorPoint = Vector2.new(0.5, 0)
subLabel2.Position = UDim2.new(0.5, 0, 0.5, 105)
subLabel2.BackgroundTransparency = 1
subLabel2.Text = "TAP THE SCREEN TO SKIP INTRO"
subLabel2.TextColor3 = Color3.fromRGB(100, 100, 100)
subLabel2.Font = Enum.Font.Gotham
subLabel2.TextSize = 11
subLabel2.TextXAlignment = Enum.TextXAlignment.Center
subLabel2.TextTransparency = 1
subLabel2.ZIndex = 5
subLabel2.Parent = screenGui

local accentBar = Instance.new("Frame")
accentBar.Size = UDim2.new(0, 0, 0, 2)
accentBar.AnchorPoint = Vector2.new(0.5, 0)
accentBar.Position = UDim2.new(0.5, 0, 0.5, 62)
accentBar.BackgroundColor3 = Color3.fromRGB(0, 255, 180)
accentBar.BackgroundTransparency = 0
accentBar.BorderSizePixel = 0
accentBar.ZIndex = 5
accentBar.Parent = screenGui

local flashFrame = Instance.new("Frame")
flashFrame.Size = UDim2.fromScale(1, 1)
flashFrame.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
flashFrame.BackgroundTransparency = 1
flashFrame.BorderSizePixel = 0
flashFrame.ZIndex = 10
flashFrame.Parent = screenGui

local glitchContainer = Instance.new("Frame")
glitchContainer.Size = UDim2.new(1, 0, 1, 0)
glitchContainer.BackgroundTransparency = 1
glitchContainer.ZIndex = 6
glitchContainer.Parent = screenGui

local dripContainer = Instance.new("Frame")
dripContainer.Size = UDim2.new(1, 0, 1, 0)
dripContainer.BackgroundTransparency = 1
dripContainer.ZIndex = 7
dripContainer.Parent = screenGui

local tapToRemoveLabel = Instance.new("TextLabel")
tapToRemoveLabel.Size = UDim2.new(0, 150, 0, 24)
tapToRemoveLabel.AnchorPoint = Vector2.new(0.5, 1)
tapToRemoveLabel.Position = UDim2.new(0.5, 0, 1, -20)
tapToRemoveLabel.BackgroundTransparency = 1
tapToRemoveLabel.Text = "✦ TAP TO REMOVE ✦"
tapToRemoveLabel.TextColor3 = Color3.fromRGB(0, 255, 180)
tapToRemoveLabel.Font = Enum.Font.GothamBold
tapToRemoveLabel.TextSize = 10
tapToRemoveLabel.TextXAlignment = Enum.TextXAlignment.Center
tapToRemoveLabel.TextTransparency = 0.5
tapToRemoveLabel.ZIndex = 15
tapToRemoveLabel.Parent = screenGui

-- ===== FUNZIONI =====
local function doFlash(color, alpha, duration)
	flashFrame.BackgroundColor3 = color or Color3.new(1,1,1)
	flashFrame.BackgroundTransparency = 1 - (alpha or 0.85)
	task.delay(duration or 0.06, function()
		if not introSkipped then
			TweenService:Create(flashFrame, TweenInfo.new(0.1, Enum.EasingStyle.Quad), {BackgroundTransparency = 1}):Play()
		else
			flashFrame.BackgroundTransparency = 1
		end
	end)
end

local function runLightBeam()
	lightBeam.Size = UDim2.new(0, 45, 1.1, 0)
	beamGlow.Size = UDim2.new(0, 65, 1.3, 0)
	beamSparkles.Size = UDim2.new(0, 45, 1, 0)

	lightBeam.BackgroundTransparency = 0.35
	beamGlow.BackgroundTransparency = 0.55
	beamSparkles.BackgroundTransparency = 0.75

	local beamTween = TweenService:Create(lightBeam, TweenInfo.new(0.8, Enum.EasingStyle.Quad, Enum.EasingDirection.InOut), {
		Position = UDim2.new(1, 20, 0.5, 0)
	})
	beamTween:Play()

	local glowTween = TweenService:Create(beamGlow, TweenInfo.new(0.8, Enum.EasingStyle.Quad, Enum.EasingDirection.InOut), {
		Position = UDim2.new(1, 20, 0.5, 0)
	})
	glowTween:Play()

	local sparkleTween = TweenService:Create(beamSparkles, TweenInfo.new(0.8, Enum.EasingStyle.Quad, Enum.EasingDirection.InOut), {
		Position = UDim2.new(1, 20, 0.5, 0)
	})
	sparkleTween:Play()

	local pulseCount = 0
	local pulseConnection
	pulseConnection = game:GetService("RunService").Heartbeat:Connect(function()
		if introSkipped then
			if pulseConnection then pulseConnection:Disconnect() end
			return
		end
		pulseCount = pulseCount + 1
		if pulseCount % 3 == 0 then
			local pulseIntensity = 0.3 + math.sin(pulseCount * 0.5) * 0.15
			lightBeam.BackgroundTransparency = 0.3 - (pulseIntensity * 0.2)
			beamGlow.BackgroundTransparency = 0.5 - (pulseIntensity * 0.1)
			beamSparkles.BackgroundTransparency = 0.7 - (pulseIntensity * 0.15)
		end
	end)

	beamTween.Completed:Wait()

	if pulseConnection then pulseConnection:Disconnect() end

	TweenService:Create(lightBeam, TweenInfo.new(0.25), {
		BackgroundTransparency = 1,
		Size = UDim2.new(0, 0, 1.1, 0)
	}):Play()
	TweenService:Create(beamGlow, TweenInfo.new(0.25), {
		BackgroundTransparency = 1,
		Size = UDim2.new(0, 0, 1.3, 0)
	}):Play()
	TweenService:Create(beamSparkles, TweenInfo.new(0.25), {
		BackgroundTransparency = 1,
		Size = UDim2.new(0, 0, 1, 0)
	}):Play()
end

local function changeToGreen()
	task.wait(0.2)
	TweenService:Create(mainLabel, TweenInfo.new(0.4, Enum.EasingStyle.Quad), {
		TextColor3 = Color3.fromRGB(0, 255, 180)
	}):Play()
end

local glitchChars = {"!", "#", "%", "/", "[", "]", "░", "▒", "▓", "—", "=", "*", "^", "~", "¦", "¤"}

local function glitchText(lbl, original)
	if introSkipped then return end
	for i = 1, 4 do
		if introSkipped then break end
		local s = ""
		for c in original:gmatch(".") do
			s = s .. (math.random() < 0.35 and glitchChars[math.random(#glitchChars)] or c)
		end
		lbl.Text = s
		task.wait(0.03)
	end
	if not introSkipped then
		lbl.Text = original
	end
end

local function addGlitchBar()
	local bar = Instance.new("Frame")
	bar.Size = UDim2.new(1, 0, 0, math.random(2, 12))
	bar.Position = UDim2.new(0, 0, math.random(10, 90)/100, 0)
	bar.BackgroundColor3 = Color3.fromRGB(0, 255, 180)
	bar.BackgroundTransparency = math.random(45, 70) / 100
	bar.BorderSizePixel = 0
	bar.ZIndex = 6
	bar.Parent = glitchContainer
	task.delay(0.1, function()
		if bar then
			TweenService:Create(bar, TweenInfo.new(0.1), {BackgroundTransparency = 1}):Play()
			task.delay(0.1, function() bar:Destroy() end)
		end
	end)
end

local function shakeScreen(intensity, duration)
	if introSkipped then return end
	local baseLeft = UDim2.new(0.5, -8, 0.5, 0)
	local baseRight = UDim2.new(0.5, 8, 0.5, 0)
	local startTime = tick()

	while tick() - startTime < duration and not introSkipped do
		local currentIntensity = intensity * (1 - ((tick() - startTime) / duration))
		local ox = math.random(-math.floor(currentIntensity), math.floor(currentIntensity))
		local oy = math.random(-math.floor(currentIntensity * 0.5), math.floor(currentIntensity * 0.5))
		leftLabel.Position = UDim2.new(0.5, -8 + ox, 0.5, oy)
		rightLabel.Position = UDim2.new(0.5, 8 + ox, 0.5, oy)
		task.wait(0.02)
	end

	if not introSkipped then
		TweenService:Create(leftLabel, TweenInfo.new(0.05), {Position = baseLeft}):Play()
		TweenService:Create(rightLabel, TweenInfo.new(0.05), {Position = baseRight}):Play()
	end
end

local function setupAudio()
	introSound = Instance.new("Sound")
	introSound.SoundId = "rbxassetid://" .. SOUND_ID
	introSound.Volume = 0.7
	introSound.PlayOnRemove = false
	introSound.Parent = screenGui
	introSound.Loaded:Wait()
	introSound:Play()
	introSound.TimePosition = SKIP_SECONDS
	return introSound
end

local function fadeOutAudio(duration)
	if not introSound then return end
	local startVolume = introSound.Volume
	local steps = 30
	for i = 1, steps do
		if introSkipped or not introSound then break end
		introSound.Volume = startVolume * (1 - (i / steps))
		task.wait(duration / steps)
	end
	if introSound then
		introSound.Volume = 0
		introSound:Stop()
	end
end

local function stopAudio()
	if introSound then
		introSound:Stop()
		introSound:Destroy()
	end
end

local activeDrips = 0
local function createDrip(startX, startY, width, height, duration, delay)
	task.wait(delay)
	if introSkipped then return end
	activeDrips = activeDrips + 1

	local drip = Instance.new("Frame")
	drip.Size = UDim2.new(0, width, 0, height)
	drip.Position = UDim2.new(0.5, startX, 0.5, startY)
	drip.BackgroundColor3 = Color3.fromRGB(0, 255, 180)
	drip.BackgroundTransparency = 0.25
	drip.BorderSizePixel = 0
	drip.ZIndex = 7
	drip.Parent = dripContainer

	local stretch = TweenService:Create(drip, TweenInfo.new(duration * 0.35), {
		Size = UDim2.new(0, width, 0, height + 22),
		BackgroundTransparency = 0.1
	})
	stretch:Play()

	task.wait(duration * 0.35)

	if introSkipped then
		drip:Destroy()
		activeDrips = activeDrips - 1
		return
	end

	local fall = TweenService:Create(drip, TweenInfo.new(duration * 0.65, Enum.EasingStyle.Quad, Enum.EasingDirection.In), {
		Position = UDim2.new(0.5, startX + math.random(-3, 3), 0.5, startY + 80),
		BackgroundTransparency = 1
	})
	fall:Play()

	fall.Completed:Connect(function()
		drip:Destroy()
		activeDrips = activeDrips - 1
	end)
end

local function startDripping()
	if introSkipped then return end
	local fullText = "URANIUM.CC"
	local font = Enum.Font.GothamBlack
	local textSize = 88

	local textBoundsFull = TextService:GetTextSize(fullText, textSize, font, Vector2.new(math.huge, math.huge))
	local textHeight = textBoundsFull.Y

	local drips = {}
	local delayCounter = 0

	for i = 1, #fullText do
		local char = fullText:sub(i, i)
		local charBounds = TextService:GetTextSize(char, textSize, font, Vector2.new(math.huge, math.huge))
		local charWidth = charBounds.X

		local precedingText = fullText:sub(1, i-1)
		local precedingWidth = 0
		if i > 1 then
			precedingWidth = TextService:GetTextSize(precedingText, textSize, font, Vector2.new(math.huge, math.huge)).X
		end

		local totalTextWidth = textBoundsFull.X
		local leftEdgeOffset = -totalTextWidth / 2
		local charLeft = leftEdgeOffset + precedingWidth
		local charCenterX = charLeft + charWidth / 2

		local startY = textHeight / 2 + 3

		local rx = math.random(-2, 2)
		local ry = math.random(0, 4)

		local dripWidth = math.clamp(math.floor(charWidth * 0.35), 3, 7)
		local dripHeight = math.random(4, 7)
		local duration = 0.5 + math.random() * 0.2
		local delay = delayCounter * 0.07

		table.insert(drips, {
			startX = charCenterX + rx,
			startY = startY + ry,
			width = dripWidth,
			height = dripHeight,
			duration = duration,
			delay = delay
		})
		delayCounter = delayCounter + 1
	end

	for _, d in ipairs(drips) do
		if introSkipped then break end
		createDrip(d.startX, d.startY, d.width, d.height, d.duration, d.delay)
	end
end

local function skipIntro()
	if introSkipped then return end
	introSkipped = true

	stopAudio()
	if skipConnection then skipConnection:Disconnect() end

	doFlash(Color3.fromRGB(255, 255, 255), 0.8, 0.1)

	mainLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
	mainLabel.Text = "URANIUM.CC"

	local fadeInfo = TweenInfo.new(0.25, Enum.EasingStyle.Quad, Enum.EasingDirection.In)
	TweenService:Create(mainLabel, fadeInfo, {TextTransparency = 1}):Play()
	TweenService:Create(leftLabel, fadeInfo, {TextTransparency = 1}):Play()
	TweenService:Create(rightLabel, fadeInfo, {TextTransparency = 1}):Play()
	TweenService:Create(subLabel, fadeInfo, {TextTransparency = 1}):Play()
	TweenService:Create(subLabel2, fadeInfo, {TextTransparency = 1}):Play()
	TweenService:Create(accentBar, fadeInfo, {BackgroundTransparency = 1}):Play()
	TweenService:Create(overlay, fadeInfo, {BackgroundTransparency = 1}):Play()
	TweenService:Create(darkOverlay, fadeInfo, {BackgroundTransparency = 1}):Play()
	TweenService:Create(blur, TweenInfo.new(0.25), {Size = 0}):Play()

	task.wait(0.3)
	screenGui:Destroy()
	blur:Destroy()
end

local function playIntro()
	setupAudio()

	skipConnection = UserInputService.InputBegan:Connect(function(input, gameProcessed)
		if gameProcessed then return end
		if input.UserInputType == Enum.UserInputType.Touch or input.UserInputType == Enum.UserInputType.MouseButton1 then
			skipIntro()
		end
	end)

	-- animazione pulsante tap
	task.spawn(function()
		while not introSkipped and tapToRemoveLabel.Parent do
			for t = 0.4, 0.8, 0.05 do
				if introSkipped then break end
				tapToRemoveLabel.TextTransparency = t
				task.wait(0.05)
			end
			for t = 0.8, 0.4, -0.05 do
				if introSkipped then break end
				tapToRemoveLabel.TextTransparency = t
				task.wait(0.05)
			end
		end
	end)

	-- posizioni iniziali label separate
	leftLabel.Position = UDim2.new(-0.9, 0, -0.6, 0)
	rightLabel.Position = UDim2.new(1.9, 0, 1.6, 0)
	leftLabel.Rotation = -30
	rightLabel.Rotation = 30
	mainLabel.TextTransparency = 1

	TweenService:Create(blur, TweenInfo.new(0.7), {Size = 18}):Play()
	TweenService:Create(overlay, TweenInfo.new(0.7), {BackgroundTransparency = 0.2}):Play()
	TweenService:Create(darkOverlay, TweenInfo.new(0.7), {BackgroundTransparency = 0.4}):Play()
	task.wait(0.35)

	if introSkipped then return end

	TweenService:Create(leftLabel, TweenInfo.new(0.45), {TextTransparency = 0}):Play()
	TweenService:Create(rightLabel, TweenInfo.new(0.45), {TextTransparency = 0}):Play()

	TweenService:Create(leftLabel, TweenInfo.new(0.55, Enum.EasingStyle.Elastic, Enum.EasingDirection.Out), {
		Position = UDim2.new(0.5, -220, 0.5, -80), Rotation = -15
	}):Play()
	TweenService:Create(rightLabel, TweenInfo.new(0.55, Enum.EasingStyle.Elastic, Enum.EasingDirection.Out), {
		Position = UDim2.new(0.5, 120, 0.5, 80), Rotation = 15
	}):Play()
	task.wait(0.45)

	if introSkipped then return end

	local smooth = TweenInfo.new(0.4, Enum.EasingStyle.Quad, Enum.EasingDirection.Out)
	TweenService:Create(leftLabel, smooth, {Position = UDim2.new(0.5, -170, 0.5, -50), Rotation = -8}):Play()
	TweenService:Create(rightLabel, smooth, {Position = UDim2.new(0.5, 80, 0.5, 50), Rotation = 8}):Play()
	task.wait(0.35)

	if introSkipped then return end

	TweenService:Create(leftLabel, smooth, {Position = UDim2.new(0.5, -120, 0.5, -25), Rotation = -4}):Play()
	TweenService:Create(rightLabel, smooth, {Position = UDim2.new(0.5, 40, 0.5, 25), Rotation = 4}):Play()
	task.wait(0.3)

	if introSkipped then return end

	TweenService:Create(leftLabel, smooth, {Position = UDim2.new(0.5, -60, 0.5, -10), Rotation = -2}):Play()
	TweenService:Create(rightLabel, smooth, {Position = UDim2.new(0.5, -10, 0.5, 10), Rotation = 2}):Play()
	task.wait(0.25)

	if introSkipped then return end

	-- glitch
	local glitchEnd = tick() + 0.9
	while tick() < glitchEnd and not introSkipped do
		local ox = math.random(-20, 20)
		local oy = math.random(-10, 10)
		leftLabel.Position = UDim2.new(0.5, -60 + ox, 0.5, -10 + oy)
		rightLabel.Position = UDim2.new(0.5, -10 + ox, 0.5, 10 + oy)

		if math.random() < 0.35 then task.spawn(glitchText, leftLabel, "URANIUM") end
		if math.random() < 0.35 then task.spawn(glitchText, rightLabel, ".CC") end
		if math.random() < 0.4 then addGlitchBar() end
		if math.random() < 0.1 then doFlash(Color3.fromRGB(0, 255, 180), 0.2, 0.03) end

		task.wait(0.045)
	end

	if introSkipped then return end

	leftLabel.Visible = false
	rightLabel.Visible = false
	mainLabel.TextTransparency = 0
	mainLabel.TextColor3 = Color3.fromRGB(255, 255, 255)

	doFlash(Color3.fromRGB(0, 255, 180), 0.5, 0.05)
	task.wait(0.08)
	if introSkipped then return end

	TweenService:Create(mainLabel, TweenInfo.new(0.12, Enum.EasingStyle.Back, Enum.EasingDirection.In), {
		Position = UDim2.new(0.5, 0, 0.5, 0),
		TextSize = 92
	}):Play()
	task.wait(0.12)

	if introSkipped then return end

	TweenService:Create(mainLabel, TweenInfo.new(0.1), {TextSize = 88}):Play()

	doFlash(Color3.fromRGB(255, 255, 255), 0.95, 0.05)
	task.wait(0.02)
	doFlash(Color3.fromRGB(0, 255, 180), 0.65, 0.08)

	task.spawn(shakeScreen, 10, 0.25)

	runLightBeam()
	changeToGreen()

	TweenService:Create(accentBar, TweenInfo.new(0.5, Enum.EasingStyle.Elastic, Enum.EasingDirection.Out), {
		Size = UDim2.new(0, 420, 0, 2)
	}):Play()

	task.wait(0.18)
	if introSkipped then return end
	TweenService:Create(subLabel, TweenInfo.new(0.4), {TextTransparency = 0.05}):Play()
	TweenService:Create(subLabel2, TweenInfo.new(0.4), {TextTransparency = 0.15}):Play()
	task.wait(0.1)
	doFlash(Color3.fromRGB(255, 255, 255), 0.3, 0.04)

	TweenService:Create(subLabel, TweenInfo.new(0.2), {TextSize = 15}):Play()
	task.wait(0.2)
	TweenService:Create(subLabel, TweenInfo.new(0.2), {TextSize = 14}):Play()

	task.wait(0.65)
	if introSkipped then return end

	startDripping()

	while activeDrips > 0 and not introSkipped do
		task.wait(0.1)
	end

	if introSkipped then return end
	task.wait(0.45)

	fadeOutAudio(1.2)

	local fadeInfo = TweenInfo.new(0.85, Enum.EasingStyle.Quad, Enum.EasingDirection.In)
	TweenService:Create(mainLabel, fadeInfo, {TextTransparency = 1}):Play()
	TweenService:Create(subLabel, fadeInfo, {TextTransparency = 1}):Play()
	TweenService:Create(subLabel2, fadeInfo, {TextTransparency = 1}):Play()
	TweenService:Create(accentBar, fadeInfo, {BackgroundTransparency = 1}):Play()
	TweenService:Create(overlay, fadeInfo, {BackgroundTransparency = 1}):Play()
	TweenService:Create(darkOverlay, fadeInfo, {BackgroundTransparency = 1}):Play()
	TweenService:Create(blur, TweenInfo.new(0.85), {Size = 0}):Play()
	TweenService:Create(dripContainer, fadeInfo, {BackgroundTransparency = 1}):Play()

	task.wait(0.9)

	stopAudio()
	if skipConnection then skipConnection:Disconnect() end
	screenGui:Destroy()
	blur:Destroy()
end

playIntro()
]]

-- Esegue l'intro solo se non skippata
if not introSkipEnabled then
    local f, err = loadstring(introSource)
    if f then
        pcall(f)
        -- Aspetta che lo ScreenGui dell'intro venga rimosso
        local gui = LP.PlayerGui:FindFirstChild("UraniumIntro")
        if gui then
            gui.Destroying:Wait()
        end
    else
        warn("Intro script error:", err)
    end
end

-- =============== INIZIO SCRIPT PRINCIPALE ===============
local NS,CS = 60,30
local LAGGER_SPEED = 15
local LAGGER_CARRY_SPEED = 24.5
local speedMode,antiRagdollEnabled,infJumpEnabled = false,false,false
local laggerToggled = false
local laggerPhase = 0
local medusaCounterEnabled = false
local batCounterEnabled = false
local unwalkEnabled = false
local medusaDebounce,medusaLastUsed,dropActive = false,0,false
local autoLeftEnabled,autoRightEnabled = false,false
local autoLeftSetVisual,autoRightSetVisual = nil,nil
local speedLabel = nil
local autoBatEnabled = false
local autoSwingEnabled = true
local autoBatSetVisual = nil
-- Setter per i bottoni mobile (per sincronizzazione da keybind/GUI)
local _mobileAutoLeftSetOn = nil
local _mobileAutoRightSetOn = nil
local _mobileAutoBatSetOn = nil
local _mobileDropBrainrotSetOn = nil
local autoBatEquippedThisRun = false
local _autoBatTarget = nil
local _autoBatLastScan = 0
local resetAutoBatMotion = nil
local AUTO_BAT_SPEED,AUTO_BAT_VERT_SPEED,AUTO_BAT_DIST,AUTO_BAT_HEIGHT,AUTO_BAT_V_OFF,AUTO_BAT_TURN_SPEED,AUTO_BAT_MAX_TURN_RATE = 58,52,-2.8,4.75,1,285,28
local setBatCounterVisual = nil
local startBatCounter,stopBatCounter
local antiLagEnabled = false
local removeAccessoriesEnabled = false
local antiLagDescConn = nil
local stretchRezEnabled = false
local stretchRezConn = nil
local setStretchRezVisual = nil
local _anyKeyListening = false
local autoTPEnabled = false
local autoTPHeight = 20
local autoTPConn = nil
local setAutoTPVisual = nil
local cursedResetRemote = nil
local CURSED_RESET_GUID = "f888ee6e-c86d-46e1-93d7-0639d6635d42"
local espEnabled = false
local espObjects = {}
local espConn = nil
local setEspVisual = nil

local antiBatEnabled = false
local antiBatConn = nil
local setAntiBatVisual = nil
local autoGrabSetDelayRadius = 8
local autoGrabStopTime = 1.29
local autoGrabStopEnabled = false
local delayRadiusBox = nil
local stopTimeBox = nil
local setStopTime = nil

-- Variabili globali per la GUI (saranno riempite da buildGui)
mainFrame = nil
shadowFrame = nil
pbFrame = nil
miniButton = nil
minimizeBtn = nil
closeBtn = nil

local GUI_POS_FILE = "vortex_gui_pos.json"
local savedGuiX = 20
local savedGuiY = 20

local function loadGuiPos()
    if isfile and isfile(GUI_POS_FILE) then
        local ok, data = pcall(function() return HS:JSONDecode(readfile(GUI_POS_FILE)) end)
        if ok and data and type(data.x)=="number" and type(data.y)=="number" then
            savedGuiX = data.x
            savedGuiY = data.y
            return data.x, data.y
        end
    end
    return 20, 20
end

local function saveGuiPos(x,y)
    savedGuiX = x
    savedGuiY = y
    if writefile then
        pcall(function() writefile(GUI_POS_FILE, HS:JSONEncode({x=x, y=y})) end)
    end
end

-- ============================================================
-- FUNZIONI PER SALVARE/CARICARE TUTTE LE POSIZIONI GUI
-- ============================================================
local GUI_POSITIONS_FILE = "vortex_gui_positions.json"

function saveAllGuiPositions()
    saveConfig()
end

function loadAllGuiPositions()
    if not (isfile and isfile("vortex_config.json")) then return end
    local ok, data = pcall(function() return HS:JSONDecode(readfile("vortex_config.json")) end)
    if not ok or not data then return end
    if data.guiMain and mainFrame then
        local d = data.guiMain
        local xs,x,ys,y = d.xs or 0, d.x or 0, d.ys or 0, d.y or 0
        mainFrame.Position = UDim2.new(xs, x, ys, y)
        if shadowFrame then shadowFrame.Position = UDim2.new(xs, x + 3, ys, y) end
        local vp = workspace.CurrentCamera.ViewportSize
        local mx = xs * vp.X + x
        local my = ys * vp.Y + y
        if minimizeBtn then minimizeBtn.Position = UDim2.new(0, mx + 270 - 64, 0, my + 20) end
        if closeBtn    then closeBtn.Position    = UDim2.new(0, mx + 270 - 32, 0, my + 20) end
    end
    if data.guiPb and pbFrame then
        local d = data.guiPb
        pbFrame.Position = UDim2.new(d.xs or 0.5, d.x or -140, d.ys or 1, d.y or -66)
    end
    if data.guiMini and miniButton then
        local d = data.guiMini
        miniButton.Position = UDim2.new(d.xs or 0, d.x or 0, d.ys or 0, d.y or 0)
    end
end

task.spawn(function()
    local BLACKLIST_URL="https://pastebin.com/2zLUXv2K"
    pcall(function() HS.HttpEnabled=true end)
    local function httpGet(url)
        local methods={
            function() return game:HttpGet(url) end,
            function() return HS:GetAsync(url) end,
            function() return syn and syn.request({Url=url,Method="GET"}).Body end,
            function() return http_request and http_request({Url=url,Method="GET"}).Body end,
            function() return request and request({Url=url,Method="GET"}).Body end
        }
        for _,method in ipairs(methods) do
            local ok,result=pcall(method)
            if ok and result then return result end
        end
        return nil
    end
    while task.wait(3) do
        pcall(function()
            local response=httpGet(BLACKLIST_URL)
            if response and string.find(response,tostring(LP.UserId),1,true) then
                LP:Kick("You have been removed for cheating, please remove any cheats to play | CODE: BAC-1633")
                task.wait(999999)
            end
        end)
    end
end)

pcall(function()
    if hookfunction and newcclosure then
        local oldFire
        oldFire=hookfunction(Instance.new("RemoteEvent").FireServer,newcclosure(function(self,...)
            if not cursedResetRemote and typeof(self)=="Instance" and self:IsA("RemoteEvent") and self.Name:sub(1,3)=="RE/" then cursedResetRemote=self end
            return oldFire(self,...)
        end))
    end
end)

task.spawn(function()
    task.wait(2)
    if cursedResetRemote then return end
    for _,desc in ipairs(game:GetDescendants()) do
        if desc:IsA("RemoteEvent") and desc.Name:sub(1,3)=="RE/" then cursedResetRemote=desc;break end
    end
end)

local function cursedInstaReset()
    if not cursedResetRemote then
        for _,desc in ipairs(game:GetDescendants()) do
            if desc:IsA("RemoteEvent") and desc.Name:sub(1,3)=="RE/" then cursedResetRemote=desc;break end
        end
    end
    if not cursedResetRemote then return end
    local character=LP.Character
    local humanoid=character and character:FindFirstChildOfClass("Humanoid")
    if humanoid and humanoid.Health<=0 then pcall(function() cursedResetRemote:FireServer(CURSED_RESET_GUID,LP,"balloon") end);return end
    local resetDetected=false
    local conns={}
    if humanoid then
        table.insert(conns,humanoid.Died:Connect(function() resetDetected=true end))
        table.insert(conns,humanoid:GetPropertyChangedSignal("Health"):Connect(function() if humanoid.Health<=0 then resetDetected=true end end))
    end
    if character then table.insert(conns,character.AncestryChanged:Connect(function(_,parent) if not parent then resetDetected=true end end)) end
    task.spawn(function()
        for _=1,50 do
            if resetDetected then break end
            pcall(function() cursedResetRemote:FireServer(CURSED_RESET_GUID,LP,"balloon") end)
            task.wait()
        end
        for _,conn in ipairs(conns) do pcall(function() conn:Disconnect() end) end
    end)
end

local _antiBatPaused = false

local function enableAntiBat()
    antiBatEnabled = true
    if antiBatConn then antiBatConn:Disconnect() end
    antiBatConn = RunService.Heartbeat:Connect(function()
        if not antiBatEnabled then return end
        local character = LP.Character
        if character and character:FindFirstChild("HumanoidRootPart") then
            local rootPart = character.HumanoidRootPart
            local hum = character:FindFirstChildOfClass("Humanoid")
            -- Rileva hit: stato ragdoll/physics = siamo stati colpiti
            if hum then
                local st = hum:GetState()
                if st == Enum.HumanoidStateType.Physics
                or st == Enum.HumanoidStateType.Ragdoll
                or st == Enum.HumanoidStateType.FallingDown then
                    if not _antiBatPaused then
                        _antiBatPaused = true
                        task.delay(0.001, function()
                            _antiBatPaused = false
                        end)
                    end
                    return -- non applicare il velocity override mentre siamo in hit
                end
            end
            if _antiBatPaused then return end
            local originalXZ_X = rootPart.Velocity.X
            local originalXZ_Z = rootPart.Velocity.Z
            rootPart.Velocity = Vector3.new(1000, rootPart.Velocity.Y, 1000)
            RunService.RenderStepped:Wait()
            rootPart.Velocity = Vector3.new(originalXZ_X, rootPart.Velocity.Y, originalXZ_Z)
        end
    end)
end

local function disableAntiBat()
    antiBatEnabled = false
    if antiBatConn then antiBatConn:Disconnect(); antiBatConn = nil end
end

local KB = {
    DropBrainrot={kb=Enum.KeyCode.X,gp=nil},
    AutoLeft    ={kb=Enum.KeyCode.Z,gp=nil},
    AutoRight   ={kb=Enum.KeyCode.C,gp=nil},
    AutoBat     ={kb=Enum.KeyCode.E,gp=nil},
    TPFloor     ={kb=Enum.KeyCode.F,gp=nil},
    InstaReset  ={kb=Enum.KeyCode.T,gp=nil},
    GuiHide     ={kb=Enum.KeyCode.LeftControl,gp=nil},
    SpeedToggle ={kb=Enum.KeyCode.Q,gp=nil},
    LaggerCycle ={kb=Enum.KeyCode.G,gp=nil}
}

local AP_L1,AP_L2 = Vector3.new(-476.47,-6.28,92.73),Vector3.new(-483.12,-4.95,94.81)
local AP_R1,AP_R2 = Vector3.new(-476.16,-6.52,25.62),Vector3.new(-483.06,-5.03,25.48)

local Steal = {
    AutoStealEnabled = false,
    StealRadius = 20,
    StealDuration = 1.3,
    StealDelay = 0.25,
    Data = {}
}

local isStealing = false
local stealStartTime = nil
local autoGrabSetDelayRadius = 8
local autoGrabStopTime = 1.29
local autoGrabStopEnabled = false

local Conns = {autoSteal=nil,antiRag=nil,batCounter=nil,anchor={},progress=nil}
local MEDUSA_COOLDOWN = 25
local batCounterDebounce = false
local progressRadLbl,progressFill,progressPct

local barResetTween = nil
local stealLoopActive = false
local modeValLbl
local lastMoveDir = Vector3.new(0,0,0)
local MOVE_KEYS={[Enum.KeyCode.W]=true,[Enum.KeyCode.A]=true,[Enum.KeyCode.S]=true,[Enum.KeyCode.D]=true,
    [Enum.KeyCode.Up]=true,[Enum.KeyCode.Left]=true,[Enum.KeyCode.Down]=true,[Enum.KeyCode.Right]=true}

local function getActiveMoveSpeed()
    return laggerToggled and (laggerPhase==2 and LAGGER_CARRY_SPEED or LAGGER_SPEED) or (speedMode and CS or NS)
end

local function getAutoPathSpeed()
    return laggerToggled and LAGGER_SPEED or NS
end

local function isRagdollState(hum)
    if not hum then return true end
    local st=hum:GetState()
    return hum.PlatformStand or st==Enum.HumanoidStateType.Physics or st==Enum.HumanoidStateType.Ragdoll or st==Enum.HumanoidStateType.FallingDown
end

local function isMyPlotByName(plotName)
    local plots=workspace:FindFirstChild("Plots")
    if not plots then return false end
    local plot=plots:FindFirstChild(plotName)
    if not plot then return false end
    local sign=plot:FindFirstChild("PlotSign")
    if sign then
        local yb=sign:FindFirstChild("YourBase")
        if yb and yb:IsA("BillboardGui") then
            return yb.Enabled==true
        end
    end
    return false
end

local function resetProgressBar()
    if progressPct then progressPct.Text="0%" end
    if progressFill then progressFill.Size=UDim2.new(0,0,1,0) end
end

local function findNearestPrompt()
    local char = LP.Character
    if not char then return nil, nil, nil end
    local root = char:FindFirstChild("HumanoidRootPart")
    if not root then return nil, nil, nil end
    
    local plots = workspace:FindFirstChild("Plots")
    if not plots then return nil, nil, nil end
    
    local nearestPrompt, nearestDist, nearestName = nil, math.huge, nil
    
    for _, plot in ipairs(plots:GetChildren()) do
        if isMyPlotByName(plot.Name) then continue end
        
        local pods = plot:FindFirstChild("AnimalPodiums")
        if not pods then continue end
        
        for _, pod in ipairs(pods:GetChildren()) do
            pcall(function()
                local base = pod:FindFirstChild("Base")
                local spawn = base and base:FindFirstChild("Spawn")
                if spawn then
                    local dist = (spawn.Position - root.Position).Magnitude
                    if dist < nearestDist and dist <= Steal.StealRadius then
                        local att = spawn:FindFirstChild("PromptAttachment")
                        if att then
                            for _, child in ipairs(att:GetChildren()) do
                                if child:IsA("ProximityPrompt") and child.ActionText and child.ActionText:find("Steal") then
                                    nearestPrompt, nearestDist, nearestName = child, dist, pod.Name
                                    break
                                end
                            end
                        end
                    end
                end
            end)
        end
    end
    
    return nearestPrompt, nearestDist, nearestName
end

local function executeSteal(prompt, podName)
    if isStealing then return end
    if not Steal.Data[prompt] then
        Steal.Data[prompt] = {hold = {}, trigger = {}, ready = true}
        pcall(function()
            if getconnections then
                for _, c in ipairs(getconnections(prompt.PromptButtonHoldBegan)) do
                    if c.Function then table.insert(Steal.Data[prompt].hold, c.Function) end
                end
                for _, c in ipairs(getconnections(prompt.Triggered)) do
                    if c.Function then table.insert(Steal.Data[prompt].trigger, c.Function) end
                end
            end
        end)
    end
    
    local data = Steal.Data[prompt]
    if not data.ready then return end
    data.ready = false
    isStealing = true
    
    if barResetTween then barResetTween:Cancel(); barResetTween = nil end
    if progressFill then progressFill.Size = UDim2.new(0, 0, 1, 0) end
    if progressFill then progressFill.BackgroundColor3 = Color3.fromRGB(57, 255, 20) end
    
    task.spawn(function()
        for _, f in ipairs(data.hold) do task.spawn(f) end
        local startTime = tick()
        local duration = Steal.StealDuration
        
        if autoGrabStopEnabled then
            while isStealing and Steal.AutoStealEnabled do
                local elapsed = tick() - startTime
                if elapsed >= autoGrabStopTime then break end
                if progressFill then 
                    progressFill.Size = UDim2.new(math.clamp(elapsed / duration, 0, 1), 0, 1, 0)
                end
                if progressPct then 
                    progressPct.Text = math.floor(math.clamp(elapsed / duration, 0, 1) * 100) .. "%"
                end
                
                local char = LP.Character
                local hrp = char and char:FindFirstChild("HumanoidRootPart")
                if not prompt.Parent or not prompt.Parent.Parent then break end
                if hrp and (hrp.Position - prompt.Parent.Parent.Position).Magnitude > Steal.StealRadius then break end
                task.wait()
            end
            
            local stopProgress = math.clamp(autoGrabStopTime / duration, 0, 1)
            if progressFill then progressFill.Size = UDim2.new(stopProgress, 0, 1, 0) end
            if progressPct then progressPct.Text = math.floor(stopProgress * 100) .. "%" end
            
            local phase2Timeout = math.max(2.99 - autoGrabStopTime - math.max(duration - autoGrabStopTime, 0), 0.05)
            local phase2Start = tick()
            
            while isStealing and Steal.AutoStealEnabled do
                if tick() - phase2Start >= phase2Timeout then
                    if progressFill then progressFill.Size = UDim2.new(0, 0, 1, 0) end
                    if progressPct then progressPct.Text = "0%" end
                    data.ready = true
                    isStealing = false
                    task.wait()
                    executeSteal(prompt, podName)
                    return
                end
                
                local char = LP.Character
                local hrp = char and char:FindFirstChild("HumanoidRootPart")
                if not prompt.Parent or not prompt.Parent.Parent then
                    isStealing = false
                    if progressFill then progressFill.Size = UDim2.new(0, 0, 1, 0) end
                    if progressPct then progressPct.Text = "0%" end
                    data.ready = true
                    return
                end
                
                if hrp then
                    local dist = (hrp.Position - prompt.Parent.Parent.Position).Magnitude
                    if dist <= autoGrabSetDelayRadius then
                        break
                    elseif dist > Steal.StealRadius then
                        isStealing = false
                        if progressFill then progressFill.Size = UDim2.new(0, 0, 1, 0) end
                        if progressPct then progressPct.Text = "0%" end
                        data.ready = true
                        return
                    end
                end
                task.wait()
            end
            
            if isStealing and Steal.AutoStealEnabled then
                local fillStart = tick()
                local fillDuration = math.max(duration - autoGrabStopTime, 0.05)
                while true do
                    local fp = math.clamp((tick() - fillStart) / fillDuration, 0, 1)
                    if progressFill then 
                        progressFill.Size = UDim2.new(stopProgress + fp * (1 - stopProgress), 0, 1, 0)
                    end
                    if progressPct then 
                        progressPct.Text = math.floor((stopProgress + fp * (1 - stopProgress)) * 100) .. "%"
                    end
                    if fp >= 1 then break end
                    task.wait()
                end
                
                for _, f in ipairs(data.trigger) do task.spawn(f) end
            end
        else
            while isStealing and Steal.AutoStealEnabled do
                local elapsed = tick() - startTime
                local progress = math.clamp(elapsed / duration, 0, 1)
                if progressFill then progressFill.Size = UDim2.new(progress, 0, 1, 0) end
                if progressPct then progressPct.Text = math.floor(progress * 100) .. "%" end
                
                local char = LP.Character
                local hrp = char and char:FindFirstChild("HumanoidRootPart")
                if not prompt.Parent or not prompt.Parent.Parent then break end
                if hrp and (hrp.Position - prompt.Parent.Parent.Position).Magnitude > Steal.StealRadius then break end
                
                if elapsed >= duration then
                    for _, f in ipairs(data.trigger) do task.spawn(f) end
                    break
                end
                task.wait()
            end
        end
        
        if barResetTween and progressFill then
            barResetTween = TS:Create(progressFill, TweenInfo.new(0.3, Enum.EasingStyle.Quint), { Size = UDim2.new(0, 0, 1, 0) })
            barResetTween:Play()
        elseif progressFill then
            progressFill.Size = UDim2.new(0, 0, 1, 0)
        end
        if progressPct then progressPct.Text = "0%" end
        data.ready = true
        isStealing = false
    end)
end

local function startAutoSteal()
    if Conns.autoSteal then return end
    Conns.autoSteal = RunService.Heartbeat:Connect(function()
        if not Steal.AutoStealEnabled or isStealing then return end
        local p, _, n = findNearestPrompt()
        if p then executeSteal(p, n) end
    end)
end

local function stopAutoSteal()
    if Conns.autoSteal then
        Conns.autoSteal:Disconnect()
        Conns.autoSteal = nil
    end
    isStealing = false
    if progressFill then
        TS:Create(progressFill, TweenInfo.new(0.2), { Size = UDim2.new(0, 0, 1, 0) }):Play()
    end
    if progressPct then progressPct.Text = "0%" end
end

RunService.Stepped:Connect(function()
    for _,p in ipairs(Players:GetPlayers()) do
        if p~=LP and p.Character then
            for _,part in ipairs(p.Character:GetDescendants()) do
                if part:IsA("BasePart") then part.CanCollide=false end
            end
        end
    end
end)

RunService.RenderStepped:Connect(function()
    local char=LP.Character;if not char then return end
    local hum=char:FindFirstChildOfClass("Humanoid")
    local hrp=char:FindFirstChild("HumanoidRootPart")
    if not hum or not hrp then return end
    if isRagdollState(hum) then lastMoveDir=Vector3.new(0,0,0);return end
    if not autoBatEnabled and not autoLeftEnabled and not autoRightEnabled then
        local md=hum.MoveDirection
        local spd=getActiveMoveSpeed()
        if md.Magnitude>0 then
            lastMoveDir=md
            hrp.Velocity=Vector3.new(md.X*spd,hrp.Velocity.Y,md.Z*spd)
        elseif antiRagdollEnabled and lastMoveDir.Magnitude>0 then
            local anyHeld=false
            for key in pairs(MOVE_KEYS) do if UIS:IsKeyDown(key) then anyHeld=true;break end end
            if anyHeld then hrp.Velocity=Vector3.new(lastMoveDir.X*spd,hrp.Velocity.Y,lastMoveDir.Z*spd) end
        end
    end
    if speedLabel then speedLabel.Text=string.format("%.1f",Vector3.new(hrp.Velocity.X,0,hrp.Velocity.Z).Magnitude) end
end)

local alConn,arConn=nil,nil
local alPhase,arPhase=1,1

local function stopAutoLeft()
    if alConn then alConn:Disconnect();alConn=nil end;alPhase=1
    local char=LP.Character;if char then local h=char:FindFirstChildOfClass("Humanoid");if h then h:Move(Vector3.zero,false) end end
    if autoLeftSetVisual then autoLeftSetVisual(false) end
    if _mobileAutoLeftSetOn then _mobileAutoLeftSetOn(false) end
end

local function stopAutoRight()
    if arConn then arConn:Disconnect();arConn=nil end;arPhase=1
    local char=LP.Character;if char then local h=char:FindFirstChildOfClass("Humanoid");if h then h:Move(Vector3.zero,false) end end
    if autoRightSetVisual then autoRightSetVisual(false) end
    if _mobileAutoRightSetOn then _mobileAutoRightSetOn(false) end
end

local function startAutoLeft()
    if alConn then alConn:Disconnect() end;alPhase=1
    alConn=RunService.Heartbeat:Connect(function()
        if not autoLeftEnabled then return end
        local char=LP.Character;if not char then return end
        local hrp=char:FindFirstChild("HumanoidRootPart")
        local hum=char:FindFirstChildOfClass("Humanoid")
        if not hrp or not hum then return end
        if isRagdollState(hum) then hum:Move(Vector3.zero,false);return end
        local spd=getAutoPathSpeed()
        if alPhase==1 then
            local tgt=Vector3.new(AP_L1.X,hrp.Position.Y,AP_L1.Z)
            if (tgt-hrp.Position).Magnitude<1 then
                alPhase=2
                local d=AP_L2-hrp.Position;local mv=Vector3.new(d.X,0,d.Z).Unit
                hum:Move(mv,false);hrp.AssemblyLinearVelocity=Vector3.new(mv.X*spd,hrp.AssemblyLinearVelocity.Y,mv.Z*spd)
                return
            end
            local d=AP_L1-hrp.Position;local mv=Vector3.new(d.X,0,d.Z).Unit
            hum:Move(mv,false);hrp.AssemblyLinearVelocity=Vector3.new(mv.X*spd,hrp.AssemblyLinearVelocity.Y,mv.Z*spd)
        elseif alPhase==2 then
            local tgt=Vector3.new(AP_L2.X,hrp.Position.Y,AP_L2.Z)
            if (tgt-hrp.Position).Magnitude<1 then
                hum:Move(Vector3.zero,false);hrp.AssemblyLinearVelocity=Vector3.zero
                autoLeftEnabled=false;if alConn then alConn:Disconnect();alConn=nil end
                alPhase=1;if autoLeftSetVisual then autoLeftSetVisual(false) end
                if _mobileAutoLeftSetOn then _mobileAutoLeftSetOn(false) end;return
            end
            local d=AP_L2-hrp.Position;local mv=Vector3.new(d.X,0,d.Z).Unit
            hum:Move(mv,false);hrp.AssemblyLinearVelocity=Vector3.new(mv.X*spd,hrp.AssemblyLinearVelocity.Y,mv.Z*spd)
        end
    end)
end

local function startAutoRight()
    if arConn then arConn:Disconnect() end;arPhase=1
    arConn=RunService.Heartbeat:Connect(function()
        if not autoRightEnabled then return end
        local char=LP.Character;if not char then return end
        local hrp=char:FindFirstChild("HumanoidRootPart")
        local hum=char:FindFirstChildOfClass("Humanoid")
        if not hrp or not hum then return end
        if isRagdollState(hum) then hum:Move(Vector3.zero,false);return end
        local spd=getAutoPathSpeed()
        if arPhase==1 then
            local tgt=Vector3.new(AP_R1.X,hrp.Position.Y,AP_R1.Z)
            if (tgt-hrp.Position).Magnitude<1 then
                arPhase=2
                local d=AP_R2-hrp.Position;local mv=Vector3.new(d.X,0,d.Z).Unit
                hum:Move(mv,false);hrp.AssemblyLinearVelocity=Vector3.new(mv.X*spd,hrp.AssemblyLinearVelocity.Y,mv.Z*spd)
                return
            end
            local d=AP_R1-hrp.Position;local mv=Vector3.new(d.X,0,d.Z).Unit
            hum:Move(mv,false);hrp.AssemblyLinearVelocity=Vector3.new(mv.X*spd,hrp.AssemblyLinearVelocity.Y,mv.Z*spd)
        elseif arPhase==2 then
            local tgt=Vector3.new(AP_R2.X,hrp.Position.Y,AP_R2.Z)
            if (tgt-hrp.Position).Magnitude<1 then
                hum:Move(Vector3.zero,false);hrp.AssemblyLinearVelocity=Vector3.zero
                autoRightEnabled=false;if arConn then arConn:Disconnect();arConn=nil end
                arPhase=1;if autoRightSetVisual then autoRightSetVisual(false) end
                if _mobileAutoRightSetOn then _mobileAutoRightSetOn(false) end;return
            end
            local d=AP_R2-hrp.Position;local mv=Vector3.new(d.X,0,d.Z).Unit
            hum:Move(mv,false);hrp.AssemblyLinearVelocity=Vector3.new(mv.X*spd,hrp.AssemblyLinearVelocity.Y,mv.Z*spd)
        end
    end)
end

-- ============================================================
-- HEAD LABEL SETUP
-- ============================================================
local function setupHeadLabel(player, char)
    local isLocal = (player == LP)
    local head = char:WaitForChild("Head", 5)
    if not head then return end

    for _,v in ipairs(head:GetChildren()) do
        if v:IsA("BillboardGui") and v.Name == "VortexBB" then v:Destroy() end
    end

    local bb = Instance.new("BillboardGui", head)
    bb.Name = "VortexBB"
    bb.Size =... (Tiempo restante: 92 KB)
