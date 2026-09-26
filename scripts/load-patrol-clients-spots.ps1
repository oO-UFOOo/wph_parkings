# SOPA Patrol Clients & QR Spots Loader
# Inserts all patrol locations for SOPA OpenEye Security System
# Date: September 26, 2026

# =========================
# CONFIGURATION
# =========================

$baseUrl = "https://sopa-openeye-security.gr/api"
$username = "ufo"
$password = "ufo_password_123"

# Create Basic Auth header
$base64Credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("$($username):$($password)"))
$headers = @{
    "Authorization" = "Basic $base64Credentials"
    "Content-Type" = "application/json"
}

# Ignore SSL certificate errors
if (-not ([System.Management.Automation.PSTypeName]'ServerCertificateValidationCallback').Type) {
    $certCallback = @"
        using System;
        using System.Net;
        using System.Net.Security;
        using System.Security.Cryptography.X509Certificates;
        public class ServerCertificateValidationCallback
        {
            public static void Ignore()
            {
                if(ServicePointManager.ServerCertificateValidationCallback ==null)
                {
                    ServicePointManager.ServerCertificateValidationCallback +=
                        delegate (
                            Object obj,
                            X509Certificate certificate,
                            X509Chain chain,
                            SslPolicyErrors errors
                        )
                        {
                            return true;
                        };
                }
            }
        }
"@
    Add-Type $certCallback
}
[ServerCertificateValidationCallback]::Ignore()

Write-Host "================================"
Write-Host "SOPA Patrol Clients & Spots Loader"
Write-Host "================================"
Write-Host ""

# =========================
# CLIENTS DATA STRUCTURE
# =========================

$clients = @(
    @{
        code = "RNL"
        name = "Renel - FB Grevena"
        description = "Renel Power Plant Grevena"
        spots = @(
            @{code = "RL-4001"; name = "FB 1 Itea"; lat = "00.000000"; lon = "00.000000"},
            @{code = "RL-4002"; name = "FB 2 Sarakina"; lat = "00.000000"; lon = "00.000000"},
            @{code = "RL-4003"; name = "FB 3 Felli"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "MTL"
        name = "Metlen Industries"
        description = "Metlen Industrial Facility"
        spots = @(
            @{code = "EN-101"; name = "Main Entrance"; lat = "00.000000"; lon = "00.000000"},
            @{code = "EN-102"; name = "Materials Warehouse"; lat = "00.000000"; lon = "00.000000"},
            @{code = "EN-103"; name = "Secondary Entrance"; lat = "00.000000"; lon = "00.000000"},
            @{code = "UR-104"; name = "Substation"; lat = "00.000000"; lon = "00.000000"},
            @{code = "UR-105"; name = "Utilities Office"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PN-106"; name = "Punta Evia"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "KFR"
        name = "Kiefer Construction"
        description = "Kiefer Construction Sites"
        spots = @(
            @{code = "KF-201"; name = "Central Construction Site"; lat = "00.000000"; lon = "00.000000"},
            @{code = "KF-202"; name = "Site 7149"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PT-203"; name = "Ptelia Location"; lat = "00.000000"; lon = "00.000000"},
            @{code = "GL-204"; name = "Galateine Facility"; lat = "00.000000"; lon = "00.000000"},
            @{code = "LG-205"; name = "Logistics Hub"; lat = "00.000000"; lon = "00.000000"},
            @{code = "FR-206"; name = "Farsala 1"; lat = "00.000000"; lon = "00.000000"},
            @{code = "FR-207"; name = "Farsala 2"; lat = "00.000000"; lon = "00.000000"},
            @{code = "FR-208"; name = "Farsala 3"; lat = "00.000000"; lon = "00.000000"},
            @{code = "AG-209"; name = "Agrinio Location"; lat = "00.000000"; lon = "00.000000"},
            @{code = "NR-210"; name = "North Solar Facility"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "KAN"
        name = "Kamt Anosis"
        description = "Kamt Anosis Construction"
        spots = @(
            @{code = "AN-301"; name = "Central Construction Site"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "TTT"
        name = "T&T Logistics"
        description = "T&T Transportation and Trading"
        spots = @(
            @{code = "TT-401"; name = "Andravida Central Warehouse"; lat = "00.000000"; lon = "00.000000"},
            @{code = "TT-402"; name = "Materials Storage"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "LRP"
        name = "Lariplast Manufacturing"
        description = "Lariplast Plastic Manufacturing"
        spots = @(
            @{code = "LR-501"; name = "Gate Entrance"; lat = "00.000000"; lon = "00.000000"},
            @{code = "LR-502"; name = "Plastic Warehouse"; lat = "00.000000"; lon = "00.000000"},
            @{code = "LR-503"; name = "Office Building"; lat = "00.000000"; lon = "00.000000"},
            @{code = "LR-504"; name = "Production Floor"; lat = "00.000000"; lon = "00.000000"},
            @{code = "LR-505"; name = "Paper Pulp 1"; lat = "00.000000"; lon = "00.000000"},
            @{code = "LR-506"; name = "Paper Pulp 2"; lat = "00.000000"; lon = "00.000000"},
            @{code = "LR-507"; name = "Administration Office"; lat = "00.000000"; lon = "00.000000"},
            @{code = "LR-508"; name = "Paper Production"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "OAS"
        name = "OASP Facilities"
        description = "OASP Management Offices"
        spots = @(
            @{code = "OS-601"; name = "Office Fax Room"; lat = "00.000000"; lon = "00.000000"},
            @{code = "OS-602"; name = "Server Rack Room"; lat = "00.000000"; lon = "00.000000"},
            @{code = "OS-603"; name = "Personnel Office"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "KMG"
        name = "Kastri Mesangala Region"
        description = "Multiple locations in Kastri and Mesangala area"
        spots = @(
            @{code = "KL-701"; name = "Kastri Junction"; lat = "00.000000"; lon = "00.000000"},
            @{code = "KL-702"; name = "Coco-Banana Store"; lat = "00.000000"; lon = "00.000000"},
            @{code = "KL-703"; name = "Mythos Location"; lat = "00.000000"; lon = "00.000000"},
            @{code = "KL-704"; name = "Kastri Plaza"; lat = "00.000000"; lon = "00.000000"},
            @{code = "MS-801"; name = "Potamaki Site"; lat = "00.000000"; lon = "00.000000"},
            @{code = "MS-802"; name = "Villa Yianna"; lat = "00.000000"; lon = "00.000000"},
            @{code = "MS-803"; name = "Totsios Property"; lat = "00.000000"; lon = "00.000000"},
            @{code = "MS-804"; name = "Rachos Location"; lat = "00.000000"; lon = "00.000000"},
            @{code = "MS-805"; name = "Nostos-Fountas"; lat = "00.000000"; lon = "00.000000"},
            @{code = "KR-901"; name = "Karavia Site"; lat = "00.000000"; lon = "00.000000"},
            @{code = "KR-902"; name = "Leventopaidа"; lat = "00.000000"; lon = "00.000000"},
            @{code = "KR-903"; name = "Dichala Location"; lat = "00.000000"; lon = "00.000000"},
            @{code = "AP-1001"; name = "Agia Paraskevi"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PK-1002"; name = "Paralia Koulouras"; lat = "00.000000"; lon = "00.000000"},
            @{code = "KL-1003"; name = "Kouloura"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PL-1004"; name = "Palaiopyrgo"; lat = "00.000000"; lon = "00.000000"},
            @{code = "RP-1101"; name = "Roupel Takidis"; lat = "00.000000"; lon = "00.000000"},
            @{code = "RP-1102"; name = "Roupel Volitsis"; lat = "00.000000"; lon = "00.000000"},
            @{code = "MO-1201"; name = "Moma Location"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "PRL"
        name = "Patrol Larisa"
        description = "Multiple patrol locations in Larisa region"
        spots = @(
            @{code = "PR-2001"; name = "Franklin Salia"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2002"; name = "Franklin Tampakika"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2003"; name = "Detox Commercial"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2004"; name = "Auto Panos"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2005"; name = "Interprom"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2006"; name = "Lobster Volos"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2007"; name = "Lobster Heroes"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2008"; name = "Lobster Neapoli"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2009"; name = "Lobster Post Office"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2010"; name = "New City Goundelitsa"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2011"; name = "Chalki Entrance Nikaia"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2012"; name = "Volos Exit"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2013"; name = "Bazoulis Warehouses"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2014"; name = "Bazoulis House"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2015"; name = "Koumbaros Property"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2016"; name = "Koumbaros House"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2017"; name = "Grandma House"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2018"; name = "Stathakos Agro Supplies"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2019"; name = "Stathakos House"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2020"; name = "Loula Plaza"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2021"; name = "Micro Store"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2022"; name = "Barberis Shop"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2023"; name = "Christos Dental Lab"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2024"; name = "Alfa Markets"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2025"; name = "Zachariou Gate"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2026"; name = "Zachariou Warehouses"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2027"; name = "Makris S.A."; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2028"; name = "Vardoulis Omorphochori"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2029"; name = "Kavá Triantafyllidis"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2030"; name = "Selfie"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2031"; name = "Agorastos Sompes"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2032"; name = "Pekas House"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2033"; name = "Pekas Mandilara Office 1"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2034"; name = "Pekas Office 2"; lat = "00.000000"; lon = "00.000000"},
            @{code = "PR-2035"; name = "Montessori Leta Nasiakopoulou"; lat = "00.000000"; lon = "00.000000"}
        )
    },
    @{
        code = "HRZ"
        name = "Horizon Energy"
        description = "Horizon Solar and Energy Facilities"
        spots = @(
            @{code = "HR-3001"; name = "Makrichori Entrance"; lat = "00.000000"; lon = "00.000000"},
            @{code = "HR-3002"; name = "Makrichori Isobox"; lat = "00.000000"; lon = "00.000000"}
        )
    }
)

# =========================
# INSERT CLIENTS
# =========================

Write-Host "Step 1: Creating Patrol Clients..."
Write-Host ""

$createdClients = @{}

foreach ($client in $clients) {
    $clientData = @{
        clientName = $client.name
        clientCode = $client.code
        description = $client.description
        isActive = $true
    }

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/patrol-clients" `
            -Method POST `
            -Headers $headers `
            -Body ($clientData | ConvertTo-Json)

        $createdClients[$client.code] = $response.id
        Write-Host "✓ Created: $($client.name) (ID: $($response.id))"
    }
    catch {
        Write-Host "✗ Error creating $($client.name): $_"
    }
}

Write-Host ""
Write-Host "Step 2: Creating QR Patrol Spots..."
Write-Host ""

$spotCount = 0

foreach ($client in $clients) {
    $clientId = $createdClients[$client.code]

    if (-not $clientId) {
        Write-Host "⚠ Skipping spots for $($client.name) - client not created"
        continue
    }

    foreach ($spot in $client.spots) {
        $spotData = @{
            spotName = $spot.name
            qrCode = $spot.code
            latitude = [double]$spot.lat
            longitude = [double]$spot.lon
            description = "$($client.name) - $($spot.name)"
            isActive = $true
            patrolClient = @{ id = $clientId }
        }

        try {
            $response = Invoke-RestMethod -Uri "$baseUrl/spots" `
                -Method POST `
                -Headers $headers `
                -Body ($spotData | ConvertTo-Json)

            Write-Host "✓ Spot: $($spot.code) - $($spot.name)"
            $spotCount++
        }
        catch {
            Write-Host "✗ Error creating spot $($spot.code): $_"
        }
    }
}

Write-Host ""
Write-Host "================================"
Write-Host "✓ Deployment Summary"
Write-Host "================================"
Write-Host "Clients Created: $($createdClients.Count)"
Write-Host "Spots Created: $spotCount"
Write-Host "================================"
Write-Host ""
Write-Host "✓ All patrol clients and spots have been loaded!"
Write-Host ""
Write-Host "Next Steps:"
Write-Host "1. Open: https://sopa-openeye-security.gr/"
Write-Host "2. Login with: ufo / ufo_password_123"
Write-Host "3. Go to Patrol Clients tab to verify"
Write-Host "4. Go to QR Spots tab to verify"
Write-Host "5. Edit GPS coordinates (currently set to 00.000000)"
Write-Host ""
