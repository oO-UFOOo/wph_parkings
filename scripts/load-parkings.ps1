# SOPA Parking API - Data Loader
# This script deletes all existing parkings and loads new parking location data

# =========================
# CONFIGURATION
# =========================

$baseUrl = "https://sopa-openeye-security.gr/api/parkings"
$username = "ufo"
$password = "ufo_password_123"

# Create Basic Auth header
$base64Credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("$($username):$($password)"))
$headers = @{
    "Authorization" = "Basic $base64Credentials"
    "Content-Type" = "application/json"
}

# Ignore SSL certificate errors (for self-signed certs)
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

# =========================
# DELETE ALL EXISTING
# =========================

Write-Host "================================"
Write-Host "SOPA Parking Data Loader"
Write-Host "================================"
Write-Host ""

Write-Host "Fetching existing parkings..."
try {
    $existingParkings = Invoke-RestMethod -Uri $baseUrl -Method GET -Headers $headers
    Write-Host "Found $($existingParkings.Count) existing parkings"
}
catch {
    Write-Host "Error fetching parkings: $_"
    Write-Host "Check credentials and URL"
    exit
}

$deleteCount = 0
foreach ($parking in $existingParkings) {
    $id = $parking.id
    if ($id) {
        try {
            Invoke-RestMethod -Uri "$baseUrl/$id" -Method DELETE -Headers $headers
            Write-Host "✓ Deleted parking ID: $id"
            $deleteCount++
        }
        catch {
            Write-Host "✗ Error deleting ID $id`: $_"
        }
    }
}

Write-Host ""
Write-Host "Deleted $deleteCount parkings"
Write-Host ""

# =========================
# NEW PARKING DATA
# =========================

$parkings = @(
    @{parkingName="manolaki1"; latitude=39.639628815672616; longitude=22.41393815390826},
    @{parkingName="manolaki2"; latitude=39.63953999935169; longitude=22.414038066186723},
    @{parkingName="papak1"; latitude=39.637238093948696; longitude=22.414992921225736},
    @{parkingName="papak2"; latitude=39.63723757755722; longitude=22.414934583183953},
    @{parkingName="papak3"; latitude=39.63724067590604; longitude=22.414874233485556},
    @{parkingName="papak4"; latitude=39.637236544774254; longitude=22.41482260096581},
    @{parkingName="papak5"; latitude=39.637234479208274; longitude=22.414783038385753},
    @{parkingName="kep thoma"; latitude=39.638221937618404; longitude=22.41415712486409},
    @{parkingName="ethnikikentro1"; latitude=39.63869622345861; longitude=22.41447893738707},
    @{parkingName="ethnikikentro2"; latitude=39.638692092413756; longitude=22.414421940449692},
    @{parkingName="odio"; latitude=39.63991373619645; longitude=22.41749295628381},
    @{parkingName="dim1"; latitude=39.637870157055026; longitude=22.414753611072335},
    @{parkingName="dim2"; latitude=39.63788731286385; longitude=22.414699604725953},
    @{parkingName="dim3"; latitude=39.637913306507095; longitude=22.41464019773322},
    @{parkingName="dim4"; latitude=39.63793670077623; longitude=22.414575390117566},
    @{parkingName="panagl1"; latitude=39.63604375343379; longitude=22.419031558456126},
    @{parkingName="panagl2"; latitude=39.63603187622372; longitude=22.419031558456126},
    @{parkingName="france"; latitude=39.63807149520708; longitude=22.420405250200776},
    @{parkingName="gnl1"; latitude=39.642094637213; longitude=22.422488647364553},
    @{parkingName="gnl2"; latitude=39.64206830309196; longitude=22.42245377864992},
    @{parkingName="gnl3"; latitude=39.64206830309197; longitude=22.42245377864992},
    @{parkingName="alkzr"; latitude=39.64264844; longitude=22.41152513}
)

# =========================
# INSERT NEW DATA
# =========================

Write-Host "Inserting $($parkings.Count) parking locations..."
Write-Host ""

$insertCount = 0
foreach ($p in $parkings) {
    $json = $p | ConvertTo-Json

    try {
        Invoke-RestMethod -Uri $baseUrl `
            -Method POST `
            -Headers $headers `
            -Body $json

        Write-Host "✓ Inserted: $($p.parkingName)"
        $insertCount++
    }
    catch {
        Write-Host "✗ Error inserting $($p.parkingName): $_"
    }
}

Write-Host ""
Write-Host "================================"
Write-Host "✓ Done! Inserted $insertCount parkings"
Write-Host "================================"
