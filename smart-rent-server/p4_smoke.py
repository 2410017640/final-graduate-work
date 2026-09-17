import json, urllib.request

BASE = "http://localhost:8081"

def call(method, path, token=None, body=None):
    url = BASE + path
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", token)
    try:
        with urllib.request.urlopen(req, timeout=10) as r:
            return r.status, json.loads(r.read().decode())
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read().decode())

# 1) login admin
s, admin = call("POST", "/user/login", body={"username": "admin", "password": "123456"})
print("admin login:", s, "role=", admin["data"]["user"]["role"])
admin_token = admin["data"]["token"]

# 2) tag list (seeded)
s, tags = call("GET", "/tag/list", token=admin_token)
print("tag/list:", s, "count=", len(tags["data"]))
print("  sample:", [(t["id"], t["name"], t["category"]) for t in tags["data"][:6]])

# 3) register a landlord
reg = {"username": "landlord_p4", "password": "123456", "role": "LANDLORD", "nickname": "房东P4"}
s, _ = call("POST", "/user/register", body=reg)
print("register landlord:", s)

# 4) login landlord
s, land = call("POST", "/user/login", body={"username": "landlord_p4", "password": "123456"})
land_token = land["data"]["token"]
print("landlord login:", s, "role=", land["data"]["user"]["role"])

# 5) publish a house with 2 tags (use first two seeded tag ids)
tag_ids = [tags["data"][0]["id"], tags["data"][3]["id"]]
house = {
    "title": "测试房源带标签", "description": "靠近地铁，精装修", "address": "测试路1号",
    "area": 45.5, "roomCount": 1, "hallCount": 1, "rent": 2200, "rentType": 1,
    "orientation": "朝南", "floor": 6, "totalFloor": 18, "images": "", "tagIds": tag_ids
}
s, pub = call("POST", "/house/publish", token=land_token, body=house)
print("publish:", s, "houseId=", pub.get("data"))
hid = pub.get("data")

# 6) my houses -> tags should be filled
s, mine = call("GET", "/house/my", token=land_token)
h = next((x for x in mine["data"] if x["id"] == hid), None)
print("my house tags:", s, [t["name"] for t in h["tags"]] if h else None)

# 7) admin delete one used tag -> relation should be removed, house keeps the other
del_id = tag_ids[0]
s, _ = call("DELETE", f"/admin/tag/{del_id}", token=admin_token)
print("admin delete tag", del_id, ":", s)
s, mine2 = call("GET", "/house/my", token=land_token)
h2 = next((x for x in mine2["data"] if x["id"] == hid), None)
print("after tag delete, house tags:", [t["name"] for t in h2["tags"]] if h2 else None)
